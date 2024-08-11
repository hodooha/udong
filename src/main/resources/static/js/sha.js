$(function(){
    url = new URL(location.href);
    urlSearch = url.searchParams;
    path = url.pathname;

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
        getRecItems();

        window.addEventListener("popstate", function(event) {
            if (event.state) {
                restoreFormState(event.state);
                updateItemList(event.state);
            }
        });

        let savedState = history.state;
        if (savedState) {
         restoreFormState(savedState);
         updateItemList(savedState);
         console.log(savedState);
        }

    }

    if(bodyId == "registerForm" || bodyId == "itemDetail"){
        datePickerActive();

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



    if(bodyId == "dreamLend"){

        window.addEventListener("popstate", function(event) {
            if (event.state) {
                restoreDreamFormState(event.state);
                updateLendList(event.state);
            }
        });

        let savedState = history.state;
        if (savedState) {
             restoreDreamFormState(savedState);
             updateLendList(savedState);
        }
    }

    if(bodyId == "dreamBorrow"){

        window.addEventListener("popstate", function(event) {
            if (event.state) {
                restoreDreamFormState(event.state);
                updateBorrowList(event.state);
            }
        });

        let savedState = history.state;
        if (savedState) {
             restoreDreamFormState(savedState);
             updateBorrowList(savedState);
        }
    }



});

function showAlerts(alert, alertType){
    if(alert == null || alert == ''){
        alert = $('#alert').val();
    }
    if(alertType == null || alert == ''){
        alertType = $('#alertType').val();
    }
    console.log(alert);
    console.log(alertType);
    if(alert){
        switch(alertType) {
            case 'success':
                return showSuccessAlert(alert);
            case 'error':
                return showErrorAlert(alert);
            case 'confirm':
                return showConfirmAlert(alert);
            default:
                return showAlert(alert);
        }
    }
}

function showSuccessAlert(alert) {
    return Swal.fire({
        title: alert,
        confirmButtonColor: "#3B5C9A",
        confirmButtonText: "확인",
        icon: "success"
    }).then(result => {
        return result.isConfirmed;
    });
}

function showErrorAlert(alert) {
    Swal.fire({
        title: alert,
        confirmButtonColor: "#3B5C9A",
        confirmButtonText: "확인",
        icon: "error"
    });
}

function showConfirmAlert(alert) {
    return Swal.fire({
        title: alert,
        showCancelButton: true,
        confirmButtonColor: "#3B5C9A",
        confirmButtonText: "확인",
        cancelButtonText: "취소",
        icon: "warning"
    }).then(result => {
        return result.isConfirmed;
    });
}

function showAlert(msg) {
    Swal.fire({
        title: msg,
        confirmButtonColor: "#3B5C9A",
        confirmButtonText: "확인",
        icon: "warning"
    });
}

function datePickerActive(){
    $('#datePicker').datepicker({
        format: 'yyyy-mm-dd',
        todayHighlight: true,
        startDate: '+1d',
        autoclose : true,
        endDate: '+1m'
    })
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


function ajax_get(reqUrl, data){

    return $.ajax({
        url: reqUrl,
        type: "get",
        data: data,
        error: function(data){
            console.log(data);
        }
    })
    .fail(function(data){
        console.log(data);
    })
    .always(function(){
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
        },
        error: function(data){
            console.log(data);
        }
    })
    .fail(function(data){
        console.log(data);
    })
    .always(function(){
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
    let title = $('#title').val();
    console.log(selectedDate);
    if(title.trim() == ""){
        showAlert("제목을 다시 입력해주세요.");
        return false;
    }
    if(isGive == 'give'){
        if(selectedDate == null || selectedDate == ''){
            showAlert("마감일을 선택해주세요.");
            return false;
        }
    }
    enableAllFileInputs();
    return true;
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

function getBorrowList(page){
    let params = getDreamSearchParams(page);
    updateBorrowList(params);

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

function updateBorrowList(params){

    let reqUrl = "/share/dream/borrowList";
    ajax_get(reqUrl, params).done(function(data){
        $('#reqDreams').replaceWith(data);
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
    $("input:radio[name='group'][value='" + params.group + "']").prop("checked", true);
    $('#availableCheck').prop('checked', params.statusCode ? true : false);
    $('#keyword').val(params.keyword);
    $('#statusSelect').val(params.statusCode).prop("selected", true);
}

function restoreDreamFormState(params) {
    $('.catSelect').val(params.catCode).prop("selected", true);
    $("input:radio[name='group'][value='" + params.group + "']").prop("checked", true);
    $('#keyword').val(params.keyword);
    $('#statusSelect').val(params.statusCode).prop("selected", true);
}

function updateItemDetail(){
    let itemGroup = url.pathname.includes("rent") ? "rent" : "give";
    let itemNo = urlSearch.get("itemNo");
    let reqUrl = `/share/${itemGroup}/updateDetail?itemNo=${itemNo}`;
    ajax_get(reqUrl).done(async function(data){
        $('#detail').replaceWith(data);
        if($('#isDeleted').val() != ''){
            if(await showErrorAlert("삭제된 물건입니다.")){
                history.back();
            };
        };
        datePickerActive();
        console.log("업데이트 성공");
    });
}

function deleteItem(item){
    if(item.statusCode == 'RNT'){
        showAlerts("대여중인 물건은 '반납완료' 처리 후 삭제가 가능합니다.");
        $('#deleteModal').modal("hide", true);
        return;
    }

    let reqUrl = `/share/delete?itemNo=${item.itemNo}&itemGroup=${item.itemGroup}`;

    location.href = reqUrl;
}

function shaRequest(item) {
    let returnDate = $('#datePicker').val();

    if(item.itemGroup == 'rent'){
        if(returnDate == ''){
            showConfirmAlert("반납희망일을 설정해주세요.");
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
    ajax_post(reqUrl, data).done(async function(result){
        let type = result.type;
        let msg = result.msg;
        if(await showAlerts(msg, type)){
            updateItemDetail();
        };
    })

}

function updateShaLike(itemNo){
    let reqUrl = `/share/like?itemNo=${itemNo}`;
    ajax_get(reqUrl).done(function(data){
        console.log(data);
        let item = data.item;
        $('#likeCnt').text(item.likeCnt);
        if(item.liked){
            $('.likeImg').attr("src", "/img/like.png");
        } else{
            $('.likeImg').attr("src", "/img/notlike.png");
        }
    })
}

function updateItStat(item){

    if(item.statusCode == "RNT"){
        showAlert("현재 대여중인 물건입니다. '반납완료' 처리 후 일시중단이 가능합니다.");
        return;
    }
    let reqUrl = `/share/updateItStat?itemNo=${item.itemNo}`

    ajax_get(reqUrl).done(function(data){
        if(path.includes("/share/dream")){
            getLendItem(item.itemNo);
        } else{
            updateItemDetail();
        }
    })

}

function getLendItem(itemNo){
    let reqUrl = "/share/dream/lendItem"
    let data = {
        itemNo: itemNo
    };

    ajax_get(reqUrl, data).done(function(result){
        let itemRow = $(`div[data-dream-id='dream${itemNo}']`);
        console.log(itemRow);
        console.log(result);
        itemRow.replaceWith(result);
    })
}

function selectReq(itemNo){
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

        $('#dreamModals').replaceWith(result);
        $('#selectRqst').modal('toggle');
    })

}

function checkReturnDate(requesters){
    let selectedRqst = $('input[name="requesters"]:checked').val();
    let seletedReq = requesters.filter((r)=>{
        return r.rqstNo == selectedRqst
    })[0];

    datePickerActive();
    $('#datePicker').val(seletedReq.returnDate);

    $('#selectRqst').modal('toggle');
    $('#approveReq').modal('toggle');

    $('#approveBtn').on("click", function(){
        let now = new Date();
        seletedReq.returnDate = $('#datePicker').val();
        if(seletedReq.returnDate == ''){
            showConfirmAlert("반납예정일을 선택해주세요.");
            return;
        };
        if(new Date(seletedReq.returnDate) < now){
            showConfirmAlert("반납예정일은 오늘 날짜 이후부터 선택 가능합니다.");
            return;
        }
        approveReq(seletedReq);
    })

    $('#backToSelectReq').on("click", function(){
        $('#approveBtn').off("click");
        $('#selectRqst').modal('toggle');
        $('#approveReq').modal('toggle');
    })
}

function approveReq(req){
    console.log(req);
    $('#approveReq').modal('toggle');

    let reqUrl = "/share/dream/approveReq";
    let data = {
       reqNo : req.reqNo,
       rqstNo: req.rqstNo,
       statusCode: "RNT",
       reqItem : req.reqItem,
       returnDate: req.returnDate
    }

    ajax_post(reqUrl, data).done(async function(result){
        let type = result.type;
        let msg = result.msg;
        if(await showAlerts(msg, type)){
            getLendItem(req.reqItem);
        };
    })
}

function sendMsg(memberNo){
    console.log(memberNo);
    window.open(`/message/sendMessageForm?memberNo=${memberNo}`, 'SendMessage', 'width=500,height=410');

    // 작성 완료 알림 이벤트
    window.addEventListener('message', async function(event) {
        if (typeof event.data === 'string' && event.data.includes('쪽지')) {
            if (await showSuccessAlert(event.data)) {
                location.reload();
            }
        }
    }, false);
}

function evalWithReturnReq(item){
    console.log(item);
    let reqUrl = "/share/dream/getRentedReq";
    let data = {
        reqItem: item.itemNo,
        statusCode: "RNT"
    }

    ajax_get(reqUrl, data).done(function(result){
        $('#evalAndReportModal').replaceWith(result);
        $('#evalModal').modal('toggle');
        $('.star_rating > .star').click(function() {
            $(this).parent().children('span').removeClass('on');
            $(this).addClass('on').prevAll('span').addClass('on');
            $('#score').val($(this).data('value'));
        })
    })
}

function postScore(req){
    let score = $('#score').val();
    console.log(score);
    console.log(req);
    let reqUrl = "/share/dream/evalWithReturnReq"
    let data = {
        reqNo: req.reqNo,
        evrNo: req.ownerNo,
        eveNo: req.rqstNo,
        rating: score,
        reqItem: req.reqItem
    }

    ajax_post(reqUrl, data).done(async function(){
        if(await showSuccessAlert("평가가 완료되었습니다.")){
            $('#evalModal').modal('toggle');
            getLendItem(req.reqItem);
        };
    })
}

function toggleDeleteModal(item){
    $('#deleteModal').modal("toggle", true);

    $('#deleteBtn').on("click", function(){
        deleteItemAtDream(item);
    })
}

function deleteItemAtDream(item) {
    let reqUrl = "/share/delete";
    let data = {
        itemNo: item.itemNo,
        itemGroup: item.itemGroup
    }

    ajax_get(reqUrl, data).done(function(){
        $('#deleteModal').modal("toggle", true);
        location.reload();
    })

}

function toggleCancelModal(req){
    $('#cancelModal').modal("toggle", true);

    $('#cancelReqBtn').on("click", function(){
        cancelReq(req);
    })

}

function cancelReq(req){
    let reqUrl = "/share/dream/deleteReq";
    let data = {
        reqNo: req.reqNo
    }

    ajax_get(reqUrl, data).done(function(){
        $('#cancelModal').modal("toggle", true);
        location.reload();

    })

}

function toggleEvalModal(req){
    console.log(req);
    $('#evalModal').modal("toggle", true);
    $('#lenderName').text(req.ownerNickname);

    $('.star_rating > .star').click(function() {
        $(this).parent().children('span').removeClass('on');
        $(this).addClass('on').prevAll('span').addClass('on');
        $('#score').val($(this).data('value'));
    })

    $('#evalBtn').on("click", function(){
        evalWithEndReq(req);

    })

}

function evalWithEndReq(req){
    let score = $('#score').val();
    let reqUrl = "/share/dream/evalWithEndReq";
    let data = {
        reqNo: req.reqNo,
        evrNo: req.rqstNo,
        eveNo: req.ownerNo,
        rating: score,
        reqItem: req.reqItem
    }

    ajax_post(reqUrl, data).done(async function(){
        await showSuccessAlert("평가가 완료되었습니다.");
        $('#evalModal').modal('toggle', true);
        location.reload();

    })
}

function toggleReportModalLend(item){
    let reqUrl = "/share/dream/getRentedReq";
    let data = {
        reqItem: item.itemNo,
        statusCode: "RNT"
    }

    ajax_get(reqUrl, data).done(function(result){
        $('#evalAndReportModal').replaceWith(result);
        $('#reportModal').modal("toggle", true);
        $('#reportBtn').on("click", function(){
            postReport();
        })
    })


}

function postReport(){
    let reqUrl = "/share/dream/insertReport";
    let data = $('#reportForm').serialize();

    ajax_post(reqUrl, data).done(function(msg){
        console.log("신고 완료");
        $('#reportModal').modal("toggle", true);
        showSuccessAlert(msg);
    })

}

function toggleReportModalBorrow(req){

    console.log(req);
    $('#reportModal').modal("toggle", true);

    $('input[id="itemTitle"]').val(req.itemDTO.title);
    $('input[name="reportedNo"]').val(req.reqNo);
    $('input[name="typeCode"]').val(req.itemDTO.itemGroup);
    $('input[name="itemNo"]').val(req.reqItem);
    $('input[id="reporterMember"]').val(req.rqstNickname);
    $('input[name="reporterMember"]').val(req.rqstNo);
    $('input[id="reportedMember"]').val(req.ownerNickname);
    $('input[name="reportedMember"]').val(req.itemDTO.ownerNo);

    $('#reportBtn').on("click", function(){
        postReport();
    })
}

function getRecItems(){

    let reqUrl = "/share/recommendItem"
    ajax_get(reqUrl).done(function(result){
        console.log("완료");
        $('#recItem').replaceWith(result);
    })
}



