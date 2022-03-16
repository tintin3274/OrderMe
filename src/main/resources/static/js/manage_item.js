let optionGroup
let option

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
        $.get( '/api/optional/all', function( data ) {
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

function getOptionOfOptionGroup(id){
    return new Promise(function (resolve,reject){
        $.get( '/api/optional/' + id, function( data ) {
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
    option = await getDataOption()
    let optionUsing = await getDataOptionUsing()

    for(i=0;i<option.length;i++){
        $('#tableOption').bootstrapTable('insertRow',{
            index: i,
            row: {
                id : i,
                idItem: option[i].id,
                name: option[i].name,
                using :optionUsing[option[i].id]
            }
        })
    }
    $('#tableOption').bootstrapTable('hideColumn', ['id','idItem'])

    optionGroup = await getDataOptionGroup()
    let optionGroupUsing = await getDataOptionGroupUsing()
    console.log(optionGroup)
    console.log(optionGroupUsing)

    for(i=0;i<optionGroup.length;i++){
        $('#tableOptionGroup').bootstrapTable('insertRow',{
            index: i,
            row: {
                id: optionGroup[i].id,
                name: optionGroup[i].name,
                using :optionGroupUsing[optionGroup[i].id],
                min:optionGroup[i].min,
                max:optionGroup[i].max
            }
        })
    }
    $('#tableOptionGroup').bootstrapTable('hideColumn', ['id'])

})

function imageFormatter(value) {
    if (value != null){
        return '<img src="/images/'+value+'" onerror="this.onerror=null;this.src=\'/images/default.png\';" />';
    }
    return '<img src="/images/default.png"/>';
}

$(document).ready(() => {
    $('#tableFood').on('click-row.bs.table',function (row, $element, field) {
        showItemModal($element)
    })
    $('#tableOptionGroup').on('click-row.bs.table',function (row, $element, field) {
        showOptionGroupModal($element)
    })
    $('#tableOption').on('click-row.bs.table',function (row, $element, field) {
        showOptionGroup($element)
    })

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

// view info with detail table
function optionDetailFormatter(index, row,$element){
    let list = row.itemList
    let text = []
    for(let i=0;i < list.length;i++){
        text.push('<p>' + list[i].name + '</p>')
    }
    return text.join('')
}

function showItemModal(item){

    if (item.image != null){
        document.getElementById('imgModal').src = '/images/'+item.image
        document.getElementById('imgModal').setAttribute('onerror',"this.src=\'/images/default.png\'")
    }
    else {document.getElementById('imgModal').src = '/images/default.png'}


    $('#tableInfo').bootstrapTable('refresh', {
        url: "/api/item/optional-of-id/" + item.id
    });
    $('#name').text(item.name)
    $('#des').text(item.description)
    $('#cate').text(item.category)
    $('#price').text(item.price)
    if(item.checkQuantity){
        $('#quan').text(item.quantity)
    }
    else {
        $('#quan').text("No")
    }
    if(item.display){
        $('#dis').text('Yes')
    }
    else {
        $('#dis').text('No')
    }

    $('#modalFood').modal('show')
}

async function showOptionGroupModal(item){
    let optional = await getOptionOfOptionGroup(item.id)
    console.log(item)
    $('#nameOptionGroup').text(item.name)
    $('#desOptionGroup').text(optional.description)
    $('#min').text(item.min)
    $('#max').text(item.max)

    $('#optionList').empty()
    for(i=0;i< optional.itemList.length;i++){
        let option =  document.createElement('li')
        option.className = 'list-group-item'
        option.innerText = optional.itemList[i].name
        $('#optionList').append(option)
    }

    $('#modalOptionGroup').modal('show')
}

function showOptionGroup(item){
    $('#idOption').text(item.idItem)
    $('#nameOption').text(item.name)
    $('#priceOption').text(option[item.id].price)
    if(option[item.id].checkQuantity){
        $('#quanOption').text(option[item.id].quantity)
    }
    else {
        $('#quanOption').text("No")
    }

    $('#modalOption').modal('show')
}

function editOptionPage(){
    $('#nameOptionEdit').val($('#nameOption').text())
    $('#priceOptionEdit').val($('#priceOption').text())
    if($('#quanOption').text() == "No"){
        document.getElementById('radioNo').setAttribute('checked',true)
    }
    else {
        document.getElementById('radioYes').setAttribute('checked',true)
        $('#qtyNum').text($('#quanOption').text())
    }
}

function editItems(){
    let check = false
    let qty = 0
    if($('#radioYes').is(":checked")){
        check = true
        qty = ('#qtyNum').text()
    }
    let json = {
        "id": $('#idOption').text(),
        "name":  $('#nameOptionEdit').val(),
        "price": $('#priceOptionEdit').val(),
        "checkQuantity": check,
        "quantity" : qty,
        "category" : "OPTION"
    }

    $.ajax({
        url: '/api/item/update',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });

}