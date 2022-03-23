let allOrder
let cookingStatus
let orderStatus

function getDataDoing(){
    return new Promise(function (resolve,reject){
        $.get( '/api/order/doing', function( data ) {
            resolve(data)
        });
    })
}

function connect() {
    var socket = new SockJS('/orderme-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/order/update', function (message) {
            console.log(JSON.parse(message.body))
            window.location.reload(true);
        });
    });
}

$( document ).ready(async function () {
    connect()
    let data = await getDataDoing()
    if(data.length > 0){
        initializeOrder(data)
    }
})

function initializeOrder(item){
    console.log(item)
    let i
    let billId = -1
    let group = []
    let title =""
    let time = ""
    for(i=0;i<item.length;i++){
        let order = item[i].order

        if((order.timestamp) == time && item[i].billId == billId){
            group.push(order)
        }
        else {
            if(group.length > 0){
                console.log(title)
                createOrder(group,title)
            }
            group = []
            group.push(order)
        }
        if(item[i].type == "DINE-IN"){
            title = "Table " + item[i].tableId + " #" + item[i].billId
        }
        else {
            title = "TAKE-OUT"+ " #" + item[i].billId
        }
        billId = item[i].billId
        time = order.timestamp
    }
    createOrder(group,title)
}

function createOrder(group,title){
    let cooking = []
    let serving = []
    let disable = ''
    let time = group[0].timestamp.split('T')[1].split(':')[0] + ':' + group[0].timestamp.split('T')[1].split(':')[1]
    for(i=0;i<group.length;i++){
        if(group[i].status == "COOKING"){
            cooking.push(createOrderItem(group[i],''))
            disable = 'disabled'
        }
        if(group[i].status == "ORDER"){
            cooking.push(createOrderItem(group[i],'disabled'))
        }
        if(group[i].status == "SERVING") {
            serving.push(createOrderItem(group[i],'checked disabled'))
        }
    }
    createOrderCard(cooking,serving,disable,time,title)
}

function createOrderCard(cooking,serving,disable,time,title){
    let btn = 'Start'
    if(disable != ''){
        btn = 'In Process'
    }
    let template = `
            <div class="rowModal">
            <div class="modal-dialog m-0">
                <div class="modal-content">
                    <div class="modal-header name-price">
                        <h5 class="modal-title" >${title}</h5>
                        <h5 class="m-0">${time}</h5>
                    </div>
                    <div class="modal-body">
                        <div>
                            ${cooking.join('')}
                        </div>
                        <del>
                            ${serving.join('')}
                        </del>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" ${disable}>${btn}</button>
                    </div>
                </div>
            </div>
        </div>
    `
    $('.containerToDo').append(template)
}

function createOrderItem(order, check){
    let template = `
    <div style="display: flex">
      <input class="form-check-input" type="checkbox" ${check} onchange="updateStatus(${order.id},'SERVING')" value=${order.id}>
    <div class="mb-2 columnFlex">
       <h6 class="m-0">${order.quantity}x ${order.name}</h6>
       <small class="text-muted">${order.option}</small>
       <small class="text-muted">${order.comment}</small>
       </div>
    </div>                       
    `
    return template
}

function updateStatus(id,status){
    $.ajax({
        url: '/api/order/update?id= ' + id + '&status=' + status,
        type: 'POST',
        success: function () {
            console.log('success')
        }
    });
}

$(document).on('click', '.modal-footer button', function () {
    var btn = $(this)
    let input = btn.closest('.modal-content').find('input')
    for(let i=0;i<input.length;i++){
        console.log(input[i].value)
        updateStatus(input[i].value,'COOKING')
    }
})