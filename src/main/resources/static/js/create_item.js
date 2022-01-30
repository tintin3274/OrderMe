var arrCategory
let clicked

$(document).ready(function() {
    $('#categoryTable').on('click-cell.bs.table', function (field, value, row, $el) {
        $('#inputCategory').val($el.name)
        $('#category').modal('hide');
    });

    $.get( '/api/item/category', function( data ) {
        arrCategory = data;
        var i;

        for(i=0;i<arrCategory.length;i++){
            console.log(arrCategory[i]);
            $('#categoryTable').bootstrapTable('insertRow',{
                index: i,
                row: {
                    name: arrCategory[i]
                }
            })
        }
    });
});

function createNewCategory(){
    var name = document.getElementById('inputNewCategory').value;
    if(name.trim() != ''){
        $('#inputCategory').val(document.getElementById('inputNewCategory').value)
        $('#category').modal('hide');
    }
}

function preview() {
    picPreview.src = URL.createObjectURL(event.target.files[0]);

}
function clearImage() {
    document.getElementById('formFile').value = null;
    picPreview.src = '/images/default.png';
}

function quantitySelect() {
    if($('#radioYes').is(":checked")){
        $('#qtyNum').prop('disabled', false);
        $('#qtyNum').prop('required',true);
    }
    else {
        $('#qtyNum').val('');
        $('#qtyNum').prop('disabled', true);
        $('#qtyNum').prop('required',false);
    }
}



function selectedOptionGroup(){
    var selectRow=$('#optionGroup').bootstrapTable('getSelections');
    var selectOptionTable=$('#selectedOption').bootstrapTable('getData');

    var selectId=[];
    var optionId=[];

    for(let i=0;i<selectRow.length;i++){
        selectId.push(selectRow[i].id);
    }
    for(let i=0;i<selectOptionTable.length;i++){
        optionId.push(selectOptionTable[i].id);
    }

    // console.log(selectId)
    // console.log(optionId)
    // console.log(selectId.filter(x => !optionId.includes(x)))

    var selectIdDiffOptionId=selectId.filter(x => !optionId.includes(x));

    for(let i=0;i<selectIdDiffOptionId.length;i++){
        var rowId = $("#selectedOption >tbody >tr").length;
        $('#selectedOption').bootstrapTable(
            'insertRow',{
                index: rowId,
                row: {
                    id: selectIdDiffOptionId[i],
                    name: selectRow.find(item => item.id === selectIdDiffOptionId[i]).name
                }
            });
    }

    // console.log(optionId.filter(x => !selectId.includes(x)))
    var optionIdDiffSelectId=optionId.filter(x => !selectId.includes(x));

    for(let i=0;i<optionIdDiffSelectId.length;i++){
        $('#selectedOption').bootstrapTable(
            'remove', {
                field: 'id',
                values: [optionIdDiffSelectId[i]]
            })
    }
}

function create() {
    let display = clicked
    let name = $('#inputName').val()
    let description = $('#inputDescription').val()
    console.log(description)
    let category = $('#inputCategory').val()
    let checkQuantity
    let quantity
    let price = $('#inputPrice').val()
    if($('#radioYes').is(":checked")){
        checkQuantity = 1
        quantity = $('#qtyNum')
    }
    else {
        checkQuantity = 0
        quantity = 0
    }
    let json ="{" + '"optionGroupId":['
    var i
    var table = $('#selectedOption').bootstrapTable('getData')
    for(i=0;i<table.length;i++){
        json += (table[i].id)
        if(i == table.length -1 ){
            break
        }
    json += ','
    }
    json += '],'

    json += '"item":{"name":"'+ name
    if(description != ''){
        json += '","description": "' + description
    }
    json += '","category":"'+ category + '","price":' + price + ',"quantity":' +
        quantity + ',"checkQuantity":' + checkQuantity + ',"display":' + display + '}}'
    json = JSON.stringify(JSON.parse(json))
    console.log(json)
    alert(json)


    var formdata = storePicture()
    formdata.append('addItemDTO',json)

    $.ajax({
        url: '/api/item/add-with-image',
        data: formdata ,
        processData: false ,
        contentType: false ,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });


    // let pictureName
    // if(document.getElementById('formFile').value == null){
    //     pictureName = 'default.png'
    // }
    // else {
    //     pictureName = $('#formFile')[0].files[0].name
    // }
    // console.log( pictureName)

}

function storePicture(){
    var img = document.getElementById('formFile');
    var imgList = img.files

    var formData = new FormData()

    if(imgList != null && imgList.length > 0){
        formData.append('image',imgList[0])
    }
    return formData
}