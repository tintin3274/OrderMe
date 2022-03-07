let formData = new FormData()

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

function checkTable(){
    $('#optionGroup').bootstrapTable('uncheckAll')
    let selected = $('#selectedOption').bootstrapTable('getData')
    let id = []
    for (let i=0;i<selected.length;i++){
        id.push(selected[i].id)
    }
    $('#optionGroup').bootstrapTable('checkBy', {field: 'id', values: id})
}

function preview() {
    picPreview.src = URL.createObjectURL(event.target.files[0]);

}
function clearImage() {
    document.getElementById('formFile').value = null;
    picPreview.src = '/images/default.png';
}

function sendApiCreateItem(jsons){
    let json = jsons
    if(json['item'].category != 'OPTION'){
        storePicture()
        alert(json['item'].category)
    }

    formData.append('addItemDTO',JSON.stringify(json))

    $.ajax({
        url: '/api/item/add-with-image',
        data: formData ,
        processData: false ,
        contentType: false ,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });
}

function storePicture(){
    var img = document.getElementById('formFile');
    var imgList = img.files

    if(imgList != null && imgList.length > 0){
        formData.append('image',imgList[0])
    }
}