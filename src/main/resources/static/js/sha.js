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

        $('.input-daterange').datepicker({
        format: 'yyyy-mm-dd',
        todayHighlight: true,
        startDate: '+1d'
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
    $('.input-daterange input').datepicker("setDate", null);
    $('.input-daterange').datepicker({
    format: 'yyyy-mm-dd',
    todayHighlight: true,
    startDate: '+1d'
    });
}

function dateRefresh() {
    $('.input-daterange input').datepicker("setDate", null);
    $('.input-daterange').datepicker({
    format: 'yyyy-mm-dd',
    todayHighlight: true,
    startDate: '+1d'
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