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

function getDataAllCategory(){
    return new Promise(function (resolve,reject){
        $.get( '/api/item/list-category' , function( data ) {
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
                price: option[i].price,
                quantity: option[i].quantity,
                using :optionUsing[option[i].id]
            }
        })
    }
    $('#tableOption').bootstrapTable('hideColumn', ['id'])

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

    $('#tableNumber').bootstrapTable('hideColumn','state')

    createTableCategory()
})

function imageFormatter(value) {
    if (value != null){
        return '<img class="img-table" src="/images/'+value+'" onerror="this.onerror=null;this.src=\'/images/default.png\';" />';
    }
    return '<img class="img-table" src="/images/default.png"/>';
}

$(document).ready(() => {
    $('#tableFood').on('click-row.bs.table',function (row, $element, field) {
        showItemModal($element)
    })
    $('#tableOptionGroup').on('click-row.bs.table',async function (row, $element, field) {
        await showOptionGroupModal($element)
    })
    $('#tableOption').on('click-row.bs.table',function (row, $element, field) {
        showOptionModal($element)
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

    $('#optionGroup').on('check.bs.table uncheck.bs.table ' +
        'check-all.bs.table uncheck-all.bs.table',
        function () {
            selectedOptionGroup()
        })
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
    $('#tableInfo').on('load-success.bs.table', function () {
        let food = $('#tableInfo').bootstrapTable('getData')
        $('#modalFood .btn-outline-primary').attr('onClick','setFoodOptionEdit('+ JSON.stringify(food) +','+ item.id +')')
    })
    $('#modalFood .btn-primary').attr('onClick','setFoodDetailEdit('+ JSON.stringify(item) +')')
    $('#alertDeleteModal .btn-danger').attr('onClick', 'deleteItem('+ item.id +')');

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
    $('#modalOptionGroup .btn-primary').attr('onClick',
        'setEditOptionGroupModal('+ JSON.stringify(optional) +')');
    $('#alertDeleteModal .btn-danger').attr('onClick','deleteOptionGroup('+ item.id +')')
}

function showOptionModal(item){
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
    $('#alertModal .btn-primary').attr('onClick', 'editOption()');
    $('#alertDeleteModal .btn-danger').attr('onClick', 'deleteItem('+ item.idItem +')');
}

function editOptionPage(){
    $('#nameOptionEdit').val($('#nameOption').text())
    $('#priceOptionEdit').val($('#priceOption').text())
    if($('#quanOption').text() == "No"){
        document.getElementById('radioNo').setAttribute('checked',true)
    }
    else {
        document.getElementById('radioYes').setAttribute('checked',true)
        $('#qtyNum').prop('disabled', false);
        $('#qtyNum').val($('#quanOption').text())
    }
}

function editOption(){
    let check = false
    let qty = 0
    if($('#radioYes').is(":checked")){
        check = true
        qty = $('#qtyNum').val()
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
            location.reload();
        }
    });

}

$(document).on('click', '#enableDeleteTable', function () {
    let btn = $(this)
    btn.toggleClass("btn-danger btn-secondary")
    $('#deleteTable').toggleClass("d-none")
    if(btn.val() == 0){
        btn.text('Cancel')
        btn.val(1)
        $('#tableNumber').bootstrapTable('showColumn','state')
        $('#tableNumber').bootstrapTable('uncheckAll')
    }
    else {
        btn.text('Delete')
        btn.val(0)
        $('#tableNumber').bootstrapTable('hideColumn','state')
    }
})

function createTable(){
    let table = $('#tableNumber').bootstrapTable('getData')
    let value = $('#inputTable').val()
    for(let i=0;i<table.length;i++){
        if(table[i].id == value){
            alert('number were used')
            return
        }
    }
    console.log(value)
    $.ajax({
        url: '/api/table/create/' + value,
        type: 'POST',
        success: function () {
            console.log('success')
            $('#tableNumber').bootstrapTable('refresh')
            $('#inputTable').val('')
        }
    });
}

function deleteTable(){
    let tables = $('#tableNumber').bootstrapTable('getSelections')
    for(let i=0;i<tables.length;i++){
        console.log(tables[i].id)
        $.ajax({
            url: '/api/table/delete/' + tables[i].id,
            type: 'DELETE',
            success: function () {
                console.log('success')
                $('#tableNumber').bootstrapTable('refresh')
            }
        });
    }
}

function deleteItem(id){
    console.log(id)
    $.ajax({
        url: '/api/item/' + id,
        type: 'DELETE',
        success: function () {
            location.reload();
        }
    });
}

function editOptionGroup(id){
    let selects = $('#selectedOption').bootstrapTable('getData'), i, selected = []
    for(i=0;i<selects.length;i++){
        selected.push(selects[i].id)
    }
    let optionGroup = {
        "id": id,
        "name": $('#inputNameGroup').val(),
        "description":$('#inputDescriptionGroup').val(),
        "min": $('#minOp').val(),
        "max":$('#maxOp').val()
    }

    let json = {
        "optionGroup": optionGroup,
        "optionId": selected
    }
    console.log(JSON.stringify(json))
    $.ajax({
        url: '/api/optional/update',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            location.reload();
        }
    });
}

function deleteOptionGroup(id){
    console.log(id)
    $.ajax({
        url: '/api/optional/' + id,
        type: 'DELETE',
        success: function () {
            location.reload();
        }
    });
}

function setEditOptionGroupModal(option){
    $('#inputNameGroup').val(option.name)
    $('#inputDescriptionGroup').val(option.description)
    $('#minOp').val(option.min)
    $('#maxOp').val(option.max)

    let items = option.itemList
    $('#selectedOption').bootstrapTable('removeAll')
    for(i=0;i<items.length;i++){
        let rowId =  $('#selectedOption').bootstrapTable('getData').length;
        $('#selectedOption').bootstrapTable(
            'insertRow',{
                index: rowId,
                row: {
                    id: items[i].id,
                    name: items[i].name,
                    price:items[i].price,
                    remove: ''
                }
            });
    }
    editMax( $('#selectedOption').bootstrapTable('getData').length)
    $('#alertModal .btn-primary').attr('onClick', 'editOptionGroup('+ option.id +')');
}

function setFoodOptionEdit(food,idFood){
    $('#optionGroup').bootstrapTable('uncheckAll');
    $('#selectedOptionGroup').bootstrapTable('removeAll')
    for(let i=0;i<food.length;i++){
        let rowId = $('#selectedOptionGroup').bootstrapTable('getData').length;
        $('#selectedOptionGroup').bootstrapTable(
            'insertRow',{
                index: rowId,
                row: {
                    id: food[i].id,
                    name: food[i].name,
                    remove: ''
                }
            });
        $('#optionGroup').bootstrapTable('updateCellByUniqueId', {
            id: food[i].id,
            field: 'state',
            value: true
        })
    }
    $('#alertModal .btn-primary').attr('onClick', 'editFoodOption('+ idFood +')');
}

function editFoodOption(id){
    let selected = $('#selectedOptionGroup').bootstrapTable('getData')
    let idOptionGroup = []
    for(let i=0;i< selected.length;i++){
        idOptionGroup.push(selected[i].id)
    }

    $.ajax({
        url: '/api/item/update-optional?id='+ id +'&optionGroupId=' + idOptionGroup.join(','),
        type: 'POST',
        success: function () {
            // location.reload();
            $('.modal').modal('hide')
            console.log('optionGroupId')
        }
    });
}

function setFoodDetailEdit(food){
    console.log(food)
    $('#inputName').val(food.name)
    $('#inputDescription').val(food.description)
    $('#inputCategory').val(food.category)
    if(food.image != null){
        document.getElementById('picPreview').src = '/images/'+food.image
        document.getElementById('picPreview').setAttribute('onerror',"this.src=\'/images/default.png\'")
    }
    else {
        document.getElementById('formFile').files= null
        document.getElementById('picPreview').src = '/images/default.png'
    }
    if(food.checkQuantity){
        document.getElementById('radioYesManage').click()
        $('#qtyNumManage').val(food.quantity)
    }
    else {
        document.getElementById('radioNoManage').click()
    }
    if(food.display){
        document.getElementById('displayYes').click()
    }
    $('#inputPrice').val(food.price)

    $('#alertModal .btn-primary').attr('onClick', 'editFoodDetail('+ food.id + ','+ JSON.stringify(food.image) +')');
}

function editFoodDetail(id, originalImg){
    let qty = 0
    let checkQuantity = false
    let display = false

    if($('#displayYes').is(":checked")){
        display = true
    }
    if($('#radioYesManage').is(":checked")){
        checkQuantity = true
        qty = $('#qtyNumManage').val()
    }

    let img = document.getElementById('formFile');
    let imgList = img.files

    let json =
    {
        "id": id,
        "name": $('#inputName').val(),
        "description": $('#inputDescription').val(),
        "category": $('#inputCategory').val(),
        "image": originalImg,
        "price": $('#inputPrice').val(),
        "quantity": qty ,
        "checkQuantity": checkQuantity,
        "display": display
    }

    $.ajax({
        url: '/api/item/update',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            if(imgList != null && imgList.length > 0){
                editFoodImage(imgList[0], id)
            }
            else {
                location.reload();
            }
        }
    });
}

function editFoodImage(img,id){
    let formData = new FormData()
    formData.append('id', id)
    formData.append('image',img)
    console.log(img)
    $.ajax({
        url: '/api/item/update-image',
        data: formData ,
        processData: false ,
        contentType: false ,
        type: 'POST',
        success: function () {
            location.reload();
        }
    });
}

function quantitySelectManage(){
    let yes = $('#radioYesManage')
    let no = $('#radioNoManage')
    let qty = $('#qtyNumManage')
    if(yes.is(":checked")){
        qty.prop('disabled', false);
        qty.prop('required',true);
    }
    else {
        qty.val('');
        qty.prop('disabled', true);
        qty.prop('required',false);
    }
}

window.operateEvent = {
    'click .remove': function (e, value, row, index) {
        $('#selectedOption').bootstrapTable('remove', {
            field: 'id',
            values: [row.id]
        })
        editMax($("#selectedOption").bootstrapTable('getData').length)
    }
}

window.operateEventItem = {
    'click .remove': function (e, value, row, index) {
        $('#selectedOptionGroup').bootstrapTable('remove', {
            field: 'id',
            values: [row.id]
        })
        $('#optionGroup').bootstrapTable('uncheckBy', {
            field: 'id',
            values: [row.id]
        })
    }
}

async function createTableCategory(){
   let categorys = await getDataAllCategory()

    for(let i=0;i<categorys.length;i++){
        $('#tableCategory').bootstrapTable('insertRow',{
            index: i,
            row: {
                state: false,
                name: categorys[i]
            }
        })
    }
    $('#tableCategory').bootstrapTable('hideColumn', ['state'])
}

$(document).on('click', '#enableReorder', function () {
    let btn = $(this)
    btn.toggleClass("btn-primary btn-secondary")
    // $('#deleteTable').toggleClass("d-none")
    if(btn.val() == 0){
        btn.text('Cancel')
        btn.val(1)
        $('#tableCategory').bootstrapTable('refreshOptions', {
            reorderableRows: true
        })
        $('#enableDeleteCategory').prop('disabled',true)
    }
    else {
        location.reload();
    }
})

$(document).on('click', '#enableDeleteCategory', function () {
    let btn = $(this)
    btn.toggleClass("btn-danger btn-secondary")
    $('#deleteCategory').toggleClass("d-none")
    if(btn.val() == 0){
        btn.text('Cancel')
        btn.val(1)
        $('#enableReorder').prop('disabled',true)
        $('#tableCategory').bootstrapTable('showColumn','state')
        $('#tableCategory').bootstrapTable('uncheckAll')
    }
    else {
        $('#enableReorder').prop('disabled',false)
        btn.text('Delete')
        btn.val(0)
        $('#tableCategory').bootstrapTable('hideColumn','state')
    }
})