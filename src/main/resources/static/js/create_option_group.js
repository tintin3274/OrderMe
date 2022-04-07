$(document).on('click', '.number-spinner button', function () {
    var btn = $(this),
        oldValue = btn.closest('.number-spinner').find('input').val().trim()
    let max = parseInt(btn.attr('value'))
    let newVal = max;

    if (btn.attr('data-dir') == 'up' ) {
        if(oldValue < max){
            newVal = parseInt(oldValue) + 1;
        }
    } else {
        if (oldValue > 1) {
            newVal = parseInt(oldValue) - 1;
        } else {
            newVal = 0;
        }
    }
    btn.closest('.number-spinner').find('input').val(newVal);
    if($('#minOp').val() > $('#maxOp').val()){
        $('#minOp').val($('#maxOp').val())
    }
});

function insertOptionTable(){
    let i
    let selected = $('#selectedOption').bootstrapTable('getData')
    let selectCheckbox =  $('#option').bootstrapTable('getSelections');

    let selectedId = []
    let selectCheckboxId = []

    for(i=0;i<selected.length;i++){
        selectedId.push(selected[i].id)
    }
    for(i=0;i<selectCheckbox.length;i++){
        selectCheckboxId.push(selectCheckbox[i].id)
    }

    let checkboxDiffSelected = selectCheckboxId.filter(x => !selectedId.includes(x))
    for(i=0;i<checkboxDiffSelected.length;i++){
        let rowId = $("#selectedOption").bootstrapTable('getData').length;
        $('#selectedOption').bootstrapTable(
            'insertRow',{
                index: rowId,
                row: {
                    id: checkboxDiffSelected[i],
                    name: selectCheckbox.find(item => item.id === checkboxDiffSelected[i]).name,
                    price:selectCheckbox.find(item => item.id === checkboxDiffSelected[i]).price,
                    remove: ''
                }
            });
    }

    let selectedDiffCheckbox = selectedId.filter(x => !selectCheckboxId.includes(x))
    // console.log(selectedDiffCheckbox)

    $('#selectedOption').bootstrapTable(
        'remove', {
            field: 'id',
            values: selectedDiffCheckbox
        })

    editMax($("#selectedOption").bootstrapTable('getData').length)
}

window.operateEvents = {
    'click .remove': function (e, value, row, index) {
        $('#selectedOption').bootstrapTable('remove', {
            field: 'id',
            values: [row.id]
        })
        editMax($("#selectedOption").bootstrapTable('getData').length)
    }
}

function editMax(max){
    let button = $('.number-spinner button')
    button.attr('value',max)
    if($('#maxOp').val() > max){
        $('#maxOp').val(max)
    }
    if($('#minOp').val() > $('#maxOp').val()){
        $('#minOp').val($('#maxOp').val())
    }
}

function createOption(){
    let checkQuantity
    let quantity
    if($('#radioYes').is(":checked")){
        checkQuantity = 1
        quantity = $('#qtyNum').val()
    }
    else {
        checkQuantity = 0
        quantity = 0
    }

    let item = {
        "name": $('#inputNameOption').val(),
        "description": null,
        "category":'OPTION',
        "price": $('#inputPrice').val(),
        "quantity": quantity,
        "checkQuantity": checkQuantity,
        "display": 1
    }

    let json = {
        "item": item,
        "optionGroupId": null
    }

    $('#option').on('load-success.bs.table', function() {
        let id = $('#option').bootstrapTable('getData').length
        checkTable()
        $('#option').bootstrapTable('check', id-1)
        insertOptionTable()
        $('.modal').modal('hide')
    });

    $.ajax({
        url: '/api/item/add',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            $('#option').bootstrapTable('refresh')
        }
    })
}

function createOptionGroup(){
    let optionGroup = {
        "name": $('#inputNameGroup').val(),
        "description":$('#inputDescriptionGroup').val(),
        "min": $('#minOp').val(),
        "max":$('#maxOp').val()
    }

    let selected = $('#selectedOption').bootstrapTable('getData')
    let selectedId = []
    for(let i=0;i<selected.length;i++){
        selectedId.push(selected[i].id)
    }

    let json = {
        "optionGroup": optionGroup,
        "optionId": selectedId
    }


    $.ajax({
        url: '/api/optional/add',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });
}