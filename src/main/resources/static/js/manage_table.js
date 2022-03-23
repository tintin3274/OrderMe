let stompClient = null;
let allTable
let index = []
let indexTable = []

function getDataBill(billId){
    return new Promise(function (resolve,reject){
        $.get( '/api/bill/' + billId, function( data ) {
            resolve(data)
        });
    })
}

function getDataAllBillTakeOut(){
    return new Promise(function (resolve,reject){
        $.get( '/api/bill/all-id-take-out-process', function( data ) {
            resolve(data)
        });
    })
}

$(document).on('click', '.number-spinner button', function () {
    var btn = $(this),
        oldValue = btn.closest('.number-spinner').find('input').val().trim()

    let newVal = oldValue;

    if (btn.attr('data-dir') == 'up' ) {
        newVal = parseInt(oldValue) + 1;
    } else {
        if (oldValue > 1) {
            newVal = parseInt(oldValue) - 1;
        } else {
            newVal = 1;
        }
    }
    btn.closest('.number-spinner').find('input').val(newVal);
});

 $( document ).ready(async function (){
    connect()

    await initializeTakeOut()

    $(' form').submit(function(e){
        openTable()
        e.preventDefault();
    });

    $('#tableFood').on('check.bs.table uncheck.bs.table ' +
        'check-all.bs.table uncheck-all.bs.table',
        function () {
            $('#completeBtn').prop('disabled', !$('#tableFood').bootstrapTable('getSelections').length)
            $('#deleteBtn').prop('disabled', !$('#tableFood').bootstrapTable('getSelections').length)
        })

})

function connect() {
    var socket = new SockJS('/orderme-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/app/table/all', function (message) {
            allTable = JSON.parse(message.body)
            initializeTable(allTable);
        },{ id: 1});
        stompClient.subscribe('/topic/table/update', function (message) {
            console.log('success')
            updateTable(JSON.parse(message.body))
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function createTableCard(numberTable,billTable){
    let template = `
                <div class="card text-center col-4">
                <div class="card-body">
                    <h5 class="card-title">${numberTable}</h5>
                    <a class="stretched-link" data-bs-toggle="modal" data-bs-target="#DineInModal" 
                    onclick="openModalDineIn(${billTable},${numberTable})"></a>
                </div>
            </div>
    `
    $('#tableContainer').append(template)
}

function initializeTable(tables){

    let i
    for(i=0;i<tables.length;i++){
        let table = tables[i]
        index.push(table.billId)
        indexTable.push(table.id)
        updatePage(table)
    }
    stompClient.unsubscribe(1)
}

async function createModalTable(billId){
     $('#deleteBtn').show()
    $(' .toolbar').show()
    $('#tableFood').bootstrapTable('removeAll')
    let bill = await getDataBill(billId)
    let orders = bill.orders
    let i
    for(i=0;i<orders.length;i++){
        $('#tableFood').bootstrapTable('insertRow',{
            index: i,
            row: {
                name: '<span class="badge rounded-pill '+orders[i].status+'" style="margin-right: 10px">'+ orders[i].status+'</span>'
                    + orders[i].name,
                id: orders[i].id,
                option: orders[i].option,
                comment: orders[i].comment,
                quantity: orders[i].quantity,
                amount: orders[i].amount
            }
        })
        if(orders[i].status == "PENDING"){
            $(' .toolbar').hide()
            $('.modal-footer').show()
        }
    }
    $('#qrBtn').prop('disabled',true)
    if(orders.length > 0 ){
        $('#cashBtn').prop('disabled',false)
    }
    else {
        $('#cashBtn').prop('disabled',true)
    }
    document.getElementById('qrBtn').onclick = function (){loadQr(bill.billId)}
    document.getElementById('completeBtn').onclick = function (){updateComplete(bill.billId)}
    document.getElementById('deleteBtn').onclick = function (){updateCancel(bill.billId)}
    document.getElementById('cashBtn').onclick = function (){payCash(bill.billId)}
    $('#total').text(bill.subTotal)
}

function initializeOption(numberTable){
    let template = `
            <option value="${numberTable}" >${numberTable}</option>
        `
    $('#createDineInModal select').append(template)
}

function updatePage(table){

    if(!table.available){
        createTableCard(table.id,table.billId)
    }
    else {
        initializeOption(table.id)
    }
}

function openTable(){
    let table = $('.form-select').val()
    let person= $('.form-control').val()

    $.ajax({
        url: '/api/table/open?id= ' + table + '&person=' + person,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });
    $('#createDineInModal').modal('hide')
}

function loadQr(billId){
    $('#qrPic').empty()
    if(allTable[index.indexOf(billId)].token != null){
        var qrcode = new QRCode(document.getElementById("qrPic"), {
            text: window.location.origin + "/dine-in/" + allTable[index.indexOf(billId)].token,
            width: 300,
            height: 300,
            colorDark : "#000000",
            colorLight : "#ffffff",
            correctLevel : QRCode.CorrectLevel.H
        });
    }
}

function orderDetailFormatter(index, row,$element){
    let text = []
    text.push('<p>' + 'option: ' + row.option + '</p>')
    text.push('<p>' + 'comment: '  + row.comment + '</p>')
    text.push('<p>' + 'quantity: '  + row.quantity + '</p>')
    text.push('<p>' + 'amount: '  + row.amount + '</p>')
    return text.join('')
}

function updateTable(table){
    let i=0

    console.log(table)
    console.log(index)
    let target = indexTable.indexOf(table.id)
    allTable[target] = table
    index[target] = table.billId
    if(!table.available){
        loadQr(table.billId)
        $('#qrModal').modal('show');
    }

    console.log(allTable)

    $('#createDineInModal select').empty()
    $('#createDineInModal select').append(`<option value="">Select Table</option>`)
    $( "#tableContainer" ).empty()
    for(i=0;i<allTable.length;i++){
        let table = allTable[i]
        updatePage(table)
    }

}

async function updateStatus(status,billId){
    let selected =$('#tableFood').bootstrapTable('getSelections')
    for(let i=0; i < selected.length;i++){
        console.log( selected[i].id)
        // let json = {
        //     "id": selected[i].id,
        //     "status": status
        // }
        // stompClient.send('/app/order/update',{},JSON.stringify(json))
        $.ajax({
            url: '/api/order/update?id= ' + selected[i].id + '&status=' + status,
            type: 'POST',
            success: function () {
                console.log('success')
            }
        });
    }
    await createModalTable(billId)
}

 async function updateComplete(billId){
    await updateStatus('COMPLETE',billId)
}

async function updateCancel(billId){
    await updateStatus('CANCEL',billId)
}

function payCash(id){
    $.ajax({
        url: '/api/payment/cash/' + id,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });
    $('#DineInModal').modal('hide')
}

async function initializeTakeOut(){
    let bill = await getDataAllBillTakeOut()
    console.log(bill)
    for(let i=0;i<bill.length;i++){
        let template = `
                    <li class="list-group-item">
                    <h5 class="card-title">Bill ID: ${bill[i]}</h5>
                    <a class="stretched-link" data-bs-toggle="modal" data-bs-target="#DineInModal" 
                    onclick="openModalTakeOut(${bill[i]})"></a>
                </li>
    `
        $('#takeOutList').append(template)
    }

}

async function openModalDineIn(billId,table){
    $('.modal-footer').show()
     await createModalTable(billId)
    $('#qrBtn').prop('disabled',false)
    $('#DineInModal h3').text('Table '+table+' Bill#' + billId)
}

async function openModalTakeOut(billId){
    $('.modal-footer').hide()
    await createModalTable(billId)
    $('#deleteBtn').hide()
    $('#DineInModal h3').text('Take Out Bill#' + billId)
}

