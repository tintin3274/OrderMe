
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


function imageFormatter(value) {
    if (value != null){
        return '<img src="/images/'+value+'" onerror="this.onerror=null;this.src=\'/images/default.png\';" />';
    }
    return '<img src="/images/default.png"/>';
}