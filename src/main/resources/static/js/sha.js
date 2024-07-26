$(function(){

        if(window.location.pathname.includes("/share/rent")){
            $('#giveBtn').addClass("gray");
            $('#rentBtn').removeClass("gray");
            $('.rentCom').css('visibility', 'visible');
        } else if(window.location.pathname.includes("/share/give")){
            $('#rentBtn').addClass("gray");
            $('#giveBtn').removeClass("gray");
            $('.rentCom').css('visibility', 'hidden');
        }

        $('#datePicker').datepicker({
        format: 'yyyy-mm-dd',
        todayHighlight: true,
        startDate: '+1d',
        autoclose : true,
        endDate: '+1m'
        })


        $("input[name='itemGroup']").change(function(){
            let checked = $("input[name='itemGroup']:checked").val();
            if(checked == 'rent'){
                $("#expiryDate").css("visibility", "hidden");
                dateRefresh();
            } else {
                $("#expiryDate").css("visibility", "visible");
                dateRefresh();
            }
        });
    });

function dateRefresh() {
    $('#datePicker').datepicker("setDate", null);
    $('#datePicker').datepicker({
    format: 'yyyy-mm-dd',
    todayHighlight: true,
    startDate: '+1d',
    autoclose : true,
    endDate: '+1m'
    });
}

function addImg() {
    let len = $(".img-group").length;
    if(len < 3){
    let addform = "<div class='img-group'><input class='form-control' type='file' name='imgs'><a href='#this' name='img-delete'>삭제</a><div>"
    $("#item-imgList").append(addform);
    $("a[name='img-delete']").on("click", function(e){
        e.preventDefault();
        deleteImg($(this));
    })}
}

function deleteImg(img){
    img.parent().remove();
}

function isValid() {
    let isGive = $('input[name=itemGroup]:checked').val();
    let selectedDate = $('#datePicker').val();
    console.log(selectedDate);
    if(isGive == 'give'){
        if(selectedDate == null || selectedDate == ''){
            alert("마감일을 선택해주세요.");
            return false;
        } else{
            return true;
        }
    }
}

function getSearchParams(page){
    let selectedCat = $('.catSelect').val();
    let group = $("input[name='group']").val();
    let locCode = $("input[name='locCode']").val();
    let isChecked = $('#availableCheck').is(':checked');
    let statusCode = isChecked ? $('#availableCheck').val() : null;
    let keyword = $('#keyword').val();
    if (page == null || page == '') {
        page = 1;
    }

    return {
        catCode: selectedCat,
        group: group,
        locCode: locCode,
        statusCode: statusCode,
        keyword: keyword,
        page: page
    };
}

function updateItems(params){
    $.ajax({
            url: "search",
            type: "get",
            data: params,
            success: function(data) {
                renderItems(data.itemList);
                renderPageNation(data.pageInfo);
                console.log(data)
            },
            error: function() {
                alert("아이템 불러오기 실패");
            }
    });
}

function renderItems(items){
    let itemResult = items.map((item) => {
        return `<div class="col">
            <div class="card" onclick="location.href='/share/${item.itemGroup}/detail?itemNo=${item.itemNo}'">
                <img src="${item.img == null ? '/img/noimg.jpg' : '/uploadFiles/' + item.img}" class="card-img-top" alt="물건이미지" style="height:20em">
                <div class="card-body">
                    <h4>${item.title}</h4>
                    <p>💛 <span>${item.likeCnt}</span>👀 <span>${item.viewCnt}</span>🙋‍♀️ <span>${item.reqCnt}</span></p>
                </div>
            </div>
        </div>`;
    }).join("");
    $("#itemList").html(itemResult);
}

function renderPageNation(pageInfo){
    let totalCounts = pageInfo.totalCounts;
    let startPage = pageInfo.startPage;
    let endPage = pageInfo.endPage;
    let totalPage = pageInfo.totalPage;
    let currentPage = pageInfo.currentPage;

    let pageResult = "";
    if(pageInfo.currentPage != 1){
        pageResult += `<li class="page-item me-1">
            <a class="page-link btn-udh-blue " href="#" aria-label="Previous">
             <span>&laquo;</span></a></li>`


    }
    `<li th:if="${pageInfo.currentPage != 1}" class="page-item me-1">
                            <a class="page-link btn-udh-blue " href="#" aria-label="Previous">
                                <span>&laquo;</span>
                            </a>
                        </li>
                        <li th:each="page : ${#numbers.sequence(pageInfo.startPage, pageInfo.endPage)}" class="page-item active me-1" aria-current="page">
                            <button th:text="${page}" class="page-link btn-udh-blue" th:onclick="search([[${page}]])"></button>
                        </li>
                        <li th:if="${pageInfo.currentPage != totalPage}" class="page-item me-1">
                            <a class="page-link btn-udh-blue " href="#" aria-label="Previous">
                                <span>&raquo;</span>
                            </a>
                        </li>
                    `
    $('.pagination').html(pageResult);
}


function search(page){
    let params = getSearchParams(page);
    console.log(params);
    updateItems(params);

}



