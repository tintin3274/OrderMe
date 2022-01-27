var arrCategory

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
    }
    else {
        $('#qtyNum').val('');
        $('#qtyNum').prop('disabled', true);
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

    console.log(selectId)
    console.log(optionId)
    console.log(selectId.filter(x => !optionId.includes(x)))

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

    console.log(optionId.filter(x => !selectId.includes(x)))
    var optionIdDiffSelectId=optionId.filter(x => !selectId.includes(x));

    for(let i=0;i<optionIdDiffSelectId.length;i++){
        $('#selectedOption').bootstrapTable(
            'remove', {
                field: 'id',
                values: [optionIdDiffSelectId[i]]
            })
    }
}