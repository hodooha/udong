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

    const bodyId = $("body").attr("id");
    if(bodyId == "giveMain" || bodyId == "rentMain"){
       const savedState = history.state;
               if (savedState) {
                   restoreFormState(savedState);
                   updateItems(savedState);
                   console.log(savedState);
               } else {
                   search(1);
               }

     window.addEventListener("popstate", function(event) {
            if (event.state) {
                restoreFormState(event.state);
                updateItems(event.state);
            }
        });

     getCatList();


    }

    if(bodyId == "itemDetail" || bodyId == "registerForm"){
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
    }

});

function getCatList() {
    $.ajax({
            url: "getCatList",
            type: "get",
            success: function(catList) {
                console.log(catList);
                renderCatList(catList);

            },
            error: function() {
                alert("카테고리 불러오기 실패");
            }
    });
}

function renderCatList(catList) {
    let catResult = catList.map( (cat) => {
        return `
            <option value=${cat.catCode}>${cat.catName}</option>
        `}).join("");

    $('.catSelect').append(catResult);

}


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
    selectedCat = $('.catSelect').val();
    let group = $("input[name='group']").val();
    let locCode = $("input[name='locCode']").val();
    let isChecked = $('#availableCheck').is(':checked');
    let statusCode = isChecked ? $('#availableCheck').val() : null;
    let keyword = $('#keyword').val();
    if (page == null || page == '') {
        page = 1;
    }
    console.log(selectedCat);
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

                // url에 검색한 쿼리들 넣어주기.
                const newUrl = createUrlWithParams(params);
                history.pushState(params, '', newUrl);
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
    pageResult += `
        <li class="page-item me-1">
            <button class="page-link btn-udh-blue" onclick="search(${currentPage-1})" style="${currentPage == 1 ? 'visibility:hidden' : ''}" aria-label="Previous">
                <span>&laquo;</span>
            </button>
        </li>`


    for(i = startPage; i <= endPage; i++){
        pageResult += `
            <li class="page-item active me-1" aria-current="page">
                <button class="page-link btn-udh-blue ${currentPage == i ? 'pageActive' : ''}" onclick="search(${i})">${i}</button>
            </li>`
    }



    pageResult += `
        <li class="page-item me-1">
           <button class="page-link btn-udh-blue " style="${currentPage == totalPage ? 'visibility:hidden' : ''}" onclick="search(${currentPage+1})" aria-label="Next">
               <span>&raquo;</span>
           </button>
        </li>`


    $('.pagination').html(pageResult);
}


function search(page){
    let params = getSearchParams(page);
    console.log(params);
    updateItems(params);

}

function createUrlWithParams(params) {
    const url = new URL(window.location.href);
    url.searchParams.set('catCode', params.catCode);
    url.searchParams.set('group', params.group);
    url.searchParams.set('locCode', params.locCode);
    url.searchParams.set('statusCode', params.statusCode);
    url.searchParams.set('keyword', params.keyword);
    url.searchParams.set('page', params.page);
    return url.toString();
}

function restoreFormState(params) {
    $('.catSelect').val(params.catCode);
    $("input[name='group']").val(params.group);
    $("input[name='locCode']").val(params.locCode);
    $('#availableCheck').prop('checked', params.statusCode ? true : false);
    $('#keyword').val(params.keyword);
}

