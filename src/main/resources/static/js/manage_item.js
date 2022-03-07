function getDataOption(){
    return new Promise(function (resolve,reject){
        $.get( '/api/item/category/OPTION', function( data ) {
            resolve(data)
        });
    })
}

function getDataOptionUsing(){
    return new Promise(function (resolve,reject){
        $.get( '/api/optional/item-using', function( data ) {
            resolve(data)
        });
    })
}

function getDataOptionGroup(){
    return new Promise(function (resolve,reject){
        $.get( '/api/optional', function( data ) {
            resolve(data)
        });
    })
}

function getDataOptionGroupUsing(){
    return new Promise(function (resolve,reject){
        $.get( '/api/optional/using', function( data ) {
            resolve(data)
        });
    })
}

$(async function() {
    let i
    $('#tableFood').bootstrapTable({
        sortReset: true,
        paginationParts: ['pageList','pageInfo']
    })
    let option = await getDataOption()
    let optionUsing = await getDataOptionUsing()

    for(i=0;i<option.length;i++){
        $('#tableOption').bootstrapTable('insertRow',{
            index: i,
            row: {
                name: option[i].name,
                using :optionUsing[option[i].id]
            }
        })
    }

    let optionGroup = await getDataOptionGroup()
    let optionGroupUsing = await getDataOptionGroupUsing()
    console.log(optionGroup)
    console.log(optionGroupUsing)

    for(i=0;i<optionGroup.length;i++){
        $('#tableOptionGroup').bootstrapTable('insertRow',{
            index: i,
            row: {
                name: optionGroup[i].name,
                using :optionGroupUsing[optionGroup[i].id],
                min:optionGroup[i].min,
                max:optionGroup[i].max
            }
        })
    }

})

function imageFormatter(value) {
    if (value != null){
        return '<img src="/images/'+value+'" onerror="this.onerror=null;this.src=\'/images/default.png\';" />';
    }
    return '<img src="/images/default.png"/>';
}

$(document).ready(() => {
    let url = location.href.replace(/\/$/, "");

    if (location.hash) {
        const hash = url.split("#");
        $('#manageTab button[href="#'+hash[1]+'"]').tab("show");
        url = location.href.replace(/\/#/, "#");
        history.replaceState(null, null, url);
        setTimeout(() => {
            $(window).scrollTop(0);
        }, 400);
    }

    $('button[data-bs-toggle="tab"]').on("click", function() {
        let newUrl;
        const hash = $(this).attr("href");
        if(hash == "#menu") {
            newUrl = url.split("#")[0];
        } else {
            newUrl = url.split("#")[0] + hash;
        }
        newUrl += "/";
        history.replaceState(null, null, newUrl);
    });
});