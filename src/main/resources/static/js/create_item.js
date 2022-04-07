var arrCategory
let clicked

$(document).ready(function() {
    $('#categoryTable').on('click-cell.bs.table', function (field, value, row, $el) {
        $('#inputCategory').val($el.name)
        $('#category').modal('hide');
    });

    $.get( '/api/item/list-category', function( data ) {
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


function selectedOptionGroup(){
    var selectRow=$('#optionGroup').bootstrapTable('getSelections');
    var selectOptionTable=$('#selectedOptionGroup').bootstrapTable('getData');

    var selectId=[];
    var optionId=[];

    for(let i=0;i<selectRow.length;i++){
        selectId.push(selectRow[i].id);
    }
    for(let i=0;i<selectOptionTable.length;i++){
        optionId.push(selectOptionTable[i].id);
    }

    var selectIdDiffOptionId=selectId.filter(x => !optionId.includes(x));

    for(let i=0;i<selectIdDiffOptionId.length;i++){
        var rowId = $("#selectedOptionGroup").bootstrapTable('getData').length;
        $('#selectedOptionGroup').bootstrapTable(
            'insertRow',{
                index: rowId,
                row: {
                    id: selectIdDiffOptionId[i],
                    name: selectRow.find(item => item.id === selectIdDiffOptionId[i]).name
                }
            });
    }

    var optionIdDiffSelectId=optionId.filter(x => !selectId.includes(x));

    $('#selectedOptionGroup').bootstrapTable(
        'remove', {
            field: 'id',
            values: optionIdDiffSelectId
        })
}

window.operateEvents = {
    'click .remove': function (e, value, row, index) {
        $('#selectedOptionGroup').bootstrapTable('remove', {
            field: 'id',
            values: [row.id]
        })
    }
}

function create() {
    let display = clicked
    let name = $('#inputName').val()
    let description = $('#inputDescription').val()
    let category = $('#inputCategory').val()
    let checkQuantity
    let quantity
    let price = $('#inputPrice').val()
    if($('#radioYes').is(":checked")){
        checkQuantity = 1
        quantity = $('#qtyNum').val()
    }
    else {
        checkQuantity = 0
        quantity = 0
    }

    let item = {
        "name": name,
        "description": description,
        "category":category,
        "price": price,
        "quantity": quantity,
        "checkQuantity": checkQuantity,
        "display": display
    }

    let group = []
    var table = $('#selectedOptionGroup').bootstrapTable('getData')
    for(i=0;i<table.length;i++){
        group.push(table[i].id)
    }
    let json = {
        "item": item,
        "optionGroupId": group
    }
    sendApiCreateItem(json)
}