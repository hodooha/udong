$(function(){
    url = new URL(location.href);
    urlSearch = url.searchParams;
    path = url.pathname;

    spinner = $('#spinner');

    if(path.includes("/share/rent")){
        $('#giveBtn').addClass("gray");
        $('#rentBtn').removeClass("gray");
        $('.rentCom').css('visibility', 'visible');
    } else if(path.includes("/share/give")){
        $('#rentBtn').addClass("gray");
        $('#giveBtn').removeClass("gray");
        $('.rentCom').css('visibility', 'hidden');
    };

    if(path.includes("/dream/lend")){
        $('#borrowerBtn').addClass("gray");
        $('#lenderBtn').removeClass("gray");
    } else if(path.includes("/dream/borrow")){
        $('#lenderBtn').addClass("gray");
        $('#borrowerBtn').removeClass("gray");
    };



    const bodyId = $("body").attr("id");
    if(bodyId == "giveMain" || bodyId == "rentMain"){
        getCatList();

        window.addEventListener("popstate", function(event) {
            if (event.state) {
                restoreFormState(event.state);
                updateItemList(event.state);
            }
        });
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
        updateItemDetail();
    }

    if(bodyId == "itemDetail"){
        updateItemDetail();
    }

    if(bodyId == "dreamLend"){

        getLendList(1);

        window.addEventListener("popstate", function(event) {
            if (event.state) {
                console.log(event.state);
                restoreFormState(event.state);
                updateLendList(event.state);
            }
        });

        let savedState = history.state;
        if (savedState) {
             restoreFormState(savedState);
             updateLendList(savedState);
             console.log(savedState);
        } else {
            getLendList(1);
        }

    }

    if(bodyId == "dreamBorrow"){
//        getBorrowList(group);
    }


});

function ajax_get(reqUrl, data){

    return $.ajax({
        url: reqUrl,
        type: "get",
        data: data,
        beforeSend: showSpinner(),
        error: function(){
            location.replace("common/errorPage");
        }
    })
    .fail(function(){
        location.replace("common/errorPage");
    })
    .always(function(){
        hideSpinner();
    });
}

function ajax_post(reqUrl, data){
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    return $.ajax({
        url: reqUrl,
        type: "post",
        data: data,
        beforeSend: function(xhr){
            xhr.setRequestHeader(header, token);
            showSpinner();
        },
        error: function(){
            location.replace("common/errorPage");
        }
    })
    .fail(function(){
        location.replace("common/errorPage");
    })
    .always(function(){
        hideSpinner();
    });
}

function hideSpinner(){
    spinner.hide();
}

function showSpinner(){
    spinner.show();
}

function getCatList() {
    let reqUrl = "/share/getCatList";
    ajax_get(reqUrl).done(function(fragment){
        $('#catSelect').replaceWith(fragment);
        const savedState = history.state;
        if (savedState) {
         restoreFormState(savedState);
         updateItemList(savedState);
         console.log(savedState);
        } else {
         search(1);
        }
    });
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

let imgCounter = 0;

function addImg() {
    let len = $(".img-group").length;
    if(len < 3){
    imgCounter++;
    let imgId = `img-${imgCounter}`;
    let addform = `
        <div class='img-group'>
            <label for='img-input-${imgCounter}'>
                <div class="btn btn-udh-silver">파일선택</div>
            </label>
            <input id='img-input-${imgCounter}' class='form-control' type='file' name='imgs'  data-img-id='${imgId}' style="display:none">
            <p id="originalName-${imgCounter}" style="display : inline-block" data-img-id='${imgId}'></p>
            <a href='#this' name='img-delete' data-img-id='${imgId}'>삭제</a>
        <div>`
    $("#item-imgList").append(addform);

    $("a[name='img-delete']").off("click").on("click", function(e){
        e.preventDefault();
        deleteImg($(this));
    })

    $("input[name='imgs']").off("change").on("change", function(e){
        e.preventDefault();
        setImgPreview($(this));
    })

    }
}

function deleteImg(img){
    let imgId = img.data('img-id');
    let hiddenInput = $(`input[data-img-id='${imgId}']`);
    if (hiddenInput.length && hiddenInput.attr('name') === 'exImgs') {
        let delFilesNo = $("#delFilesNo").val();
        $("#delFilesNo").val(delFilesNo + hiddenInput.val() + ",");
        hiddenInput.val('');
    }
    if (hiddenInput.length && hiddenInput.attr('name') === 'exImgsName') {
        let delFilesName = $("#delFilesName").val();
        $("#delFilesName").val(delFilesName + hiddenInput.val() + ",");
        hiddenInput.val('');
    }

    $(`#${imgId}`).parent().remove();
    img.parent().remove();
}

function setImgPreview(input){
    if(input[0].files && input[0].files[0]){
        let reader = new FileReader();
        reader.onload = function(e){
            let imgId = input.data('img-id');
            let img = document.createElement("img");
            img.setAttribute("src", e.target.result);
            img.setAttribute("style", "height:60px");
            img.setAttribute("id", imgId);

            let imgDiv = document.createElement("div");
            imgDiv.setAttribute("id", `${imgId}-div`);
            imgDiv.appendChild(img);

            $('#imgPreview').append(imgDiv);
        };
        reader.readAsDataURL(input[0].files[0]);
        let imgId = input.data('img-id');
        $(`p[data-img-id="${imgId}"]`).html(input[0].files[0].name);
        input.prop('disabled', true);
    }

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

function enableAllFileInputs() {
    $("input[name='imgs']").prop('disabled', false);
}

function getSearchParams(page){
    selectedCat = $('.catSelect').val();
    let group = $("input[name='group']").val();
    let isChecked = $('#availableCheck').is(':checked');
    let statusCode = isChecked ? $('#availableCheck').val() : '';
    let keyword = $('#keyword').val();
    if (page == null || page == '') {
        page = 1;
    }
    console.log(selectedCat);
    return {
        catCode: selectedCat,
        group: group,
        statusCode: statusCode,
        keyword: keyword,
        page: page
    };
}

function updateItemList(params){
    let reqUrl = "/share/search";
    ajax_get(reqUrl, params).done(function(fragment){
        $('#itemList').replaceWith(fragment);

        // url에 검색한 쿼리들 넣어주기.
        let newUrl = createUrlWithParams(params);
        history.pushState(params, '', newUrl);

    });
}

function search(page){
    let params = getSearchParams(page);
    updateItemList(params);
}

function getLendList(page){
    let params = getDreamSearchParams(page);
    updateLendList(params);
}

function getDreamSearchParams(page){
    let catCode = $('.catSelect').val();
    let group = $("input[name='group']:checked").val();
    let statusCode = $('#statusSelect').val();
    let keyword = $('#keyword').val();
    if (page == null || page == '') {
        page = 1;
    }

    return {
        catCode: catCode,
        group: group,
        statusCode: statusCode,
        keyword: keyword,
        page: page
    };

}

function updateLendList(params){

    let reqUrl = "/share/dream/lendList";
    ajax_get(reqUrl, params).done(function(data){
        $('#dreams').replaceWith(data);
        let newUrl = createUrlWithParams(params);
        history.pushState(params, '', newUrl);
    })
}

function createUrlWithParams(params) {
    url.searchParams.set('catCode', params.catCode);
    url.searchParams.set('group', params.group);
    url.searchParams.set('statusCode', params.statusCode);
    url.searchParams.set('keyword', params.keyword);
    url.searchParams.set('page', params.page);
    return url.toString();
}

function restoreFormState(params) {
    $('.catSelect').val(params.catCode).prop("selected", true);
    $("input[name='group']").val(params.group);
    $('#availableCheck').prop('checked', params.statusCode ? true : false);
    $('#keyword').val(params.keyword);
    $('#statusSelect').val(params.statusCode).prop("selected", true);
}

function updateItemDetail(){
    let itemGroup = url.pathname.includes("rent") ? "rent" : "give";
    let itemNo = urlSearch.get("itemNo");
    let reqUrl = `/share/${itemGroup}/updateDetail?itemNo=${itemNo}`;
    ajax_get(reqUrl).done(function(data){
        $('#detail').replaceWith(data);
    });
}

function shaRequest(item) {
    let returnDate = $('#datePicker').val();

    if(item.itemGroup == 'rent'){
        if(returnDate == ''){
            alert("반납희망일을 설정해주세요.");
            return;
        }
    }

    let data = {
        reqItem: item.itemNo,
        returnDate: returnDate,
        reqGroup: item.itemGroup,
        ownerNo: item.ownerNo
    }

    insertReq(data);
}

function insertReq(data){
    let reqUrl = "/share/request";
    ajax_post(reqUrl, data).done(function(msg){
        updateItemDetail();
        alert(msg);
    })
}

function updateShaLike(itemNo){
    let reqUrl = `/share/like?reqItem=${itemNo}`;
    ajax_get(reqUrl).done(function(data){
        updateItemDetail();
    })
}

function updateItStat(item){

    if(item.statusCode == "RNT"){
        alert("현재 대여중인 물건입니다. '반납완료' 처리 후 일시중단이 가능합니다.");
        return;
    }
    let reqUrl = `/share/updateItStat?itemNo=${item.itemNo}`
    ajax_get(reqUrl).done(function(){
        updateItemDetail();
    })

}

function approveReq(itemNo){
    console.log(itemNo);
    getRequesters(itemNo);
}

function getRequesters(itemNo){
    let reqUrl = "/share/dream/requesters";
    let data = {
        reqItem: itemNo,
        statusCode: "RQD"
    }
    ajax_get(reqUrl, data).done(function(result){
        $('#selectRqst').replaceWith(result);
        $('#selectRqst').modal('show');
    })



}


