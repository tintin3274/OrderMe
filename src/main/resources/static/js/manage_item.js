
function tog(){
    $('.slideBar').toggleClass('slideHide');
}


$(function() {
    $('#tableFood').bootstrapTable({
        sortReset: true,
        paginationParts: ['pageList','pageInfo']
    })
})

function selectFood(){
    $('#pageFood').show()
}

function selectOpGr(){
    $('#pageFood').hide();
}

function selectOption(){
    $('#pageFood').hide();
}
