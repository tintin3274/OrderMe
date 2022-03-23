let item
let allItem
let groupName
let idItem
let orderRequests = []
let cartText = []
let startPrice
let optionPrice
let optionPriceLabel
let totalPrice
let maxQty = 10

function getCategoryData(){
    return new Promise(function (resolve, reject){
        $.get( '/api/item/list-category', function( data ) {
            resolve(data)
        });
    })
}

function getItemEachCategoryData(category){
    return new Promise(function (resolve, reject){
        $.get( '/api/item/category/'+ category, function( data ) {
            resolve(data)
        });
    })
}

function getBillData(){
    return new Promise(function (resolve, reject){
        $.get('/api/bill/my-bill',function (data){
            resolve(data)
        })
    })
}

$( document ).ready(async function() {
    $('footer').hide()

    let userType = $('title').text()
    if(userType == 'TAKE-OUT'){
        $('#billNav').hide()
        $('#cartBtn').text('Payment')
    }

    let allCategory = await getCategoryData()
    for(let i=0; i < allCategory.length;i++){
        let category = allCategory[i]
        createNavCategory(category)
        item = await getItemEachCategoryData(category)
        // console.log(category)
        // console.log(item)
        await createCard(category)
    }
});


function createCard( category ){

    let categoryName = document.createElement('h3')
    categoryName.innerText = category
    categoryName.id = category

    let cardScroll = document.createElement('div')
    cardScroll.className = 'cardScroll'

    let cardContainer = document.createElement('div')
    cardContainer.className = 'row'

    for(let i=0;i<item.length;i++){
        if(item[i].display == 1){
            let id = item[i].id

            let card = document.createElement('div');
            card.className = 'col-md-6 card d-flex';

            let detail = document.createElement('div');
            detail.className ='row my-1'

            let pictureFrame =document.createElement('div');
            pictureFrame.className = 'columnImage col-auto'

            var picture = document.createElement('img');
            picture.className = 'picture';
            picture.id = 'img'+ id
            if (item[i].image != null){
                // console.log(item[i].image)
                picture.src = '/images/'+item[i].image
                picture.setAttribute('onerror',"this.src=\'/images/default.png\'")
            }
            else {picture.src = '/images/default.png'}

            let cardBody = document.createElement('div');
            cardBody.className = 'col-7 card-body'

            let title = document.createElement('h5');
            title.innerText = item[i].name;
            title.className = 'card-title menu-name';

            let cardText = document.createElement('p');
            cardText.className = 'card-text'

            let text = document.createElement('small');
            text.className = 'text-muted hide-text'
            text.innerText = item[i].description

            let prices = document.createElement('h6');
            prices.className = 'price'
            prices.innerText = '฿ '+item[i].price

            let link = document.createElement('a');
            link.className = 'stretched-link'
            link.setAttribute('data-bs-toggle','modal')
            link.setAttribute('data-bs-target',"#staticBackdrop")
            link.id = id
            link.onclick = async function(){
                await createModal(id)
                $('#nameButton').text('Get in Cart')
                document.getElementById('closeModal').onclick = null
                document.getElementById('goCart').onclick = function () {addCart()}
            }

            pictureFrame.appendChild(picture)
            cardBody.appendChild(title)
            cardText.appendChild(text)
            cardBody.appendChild(cardText)
            cardBody.appendChild(link)
            cardBody.appendChild(prices)
            detail.appendChild(pictureFrame)
            detail.appendChild(cardBody)
            card.appendChild(detail)
            cardContainer.appendChild(card)
        }
    }
    cardScroll.appendChild(cardContainer)
    $('#menu').append(categoryName)
    $('#menu').append(cardScroll)
}

function createNavCategory( category ){

    let navItem = document.createElement('li')
    navItem.className = 'nav-item'

    let navLink =  document.createElement('a')
    navLink.className = 'nav-link'
    navLink.href = '#' + category
    navLink.innerText = category

    navItem.appendChild(navLink)
    $('#navbar-category').append(navItem)
}

function getItemData(id){
    return new Promise( function (resolve, reject){
        $.get( '/api/item/' + id ,function( data ) {
            resolve(data)
        });}
    )
}

async function createModal(id){

    idItem = id
    // console.log(idItem)
    allItem = await getItemData(id)
    startPrice = allItem.price
    if(allItem.checkQuantity){
        maxQty = allItem.quantity
    }

    $('#inputQuantity').val(1)
        // console.log(allItem)
    $('#titleItem').text(allItem.name)
    $('#desItem').text(allItem.description)
    $('#priceItem').text('฿ ' + startPrice)
    $('#modalBody').empty()
    $('#inputComment').val(null)
    let optionData = allItem.optionalList
    createOption(optionData)

    if (allItem.image != null){
        document.getElementById('imgModal').src = '/images/'+allItem.image
        document.getElementById('imgModal').setAttribute('onerror',"this.src=\'/images/default.png\'")
    }
    else {document.getElementById('imgModal').src = '/images/default.png'}

}

function createOption(optionItem){

    groupName = {}

    // console.log(optionItem)
    let optionContainer = document.createElement('div');

    if(optionItem.length > 0){
        let line = document.createElement('hr');
        line.className = "mb-0"
        $('#modalBody').append(line)
    }

    for(let i=0;i < optionItem.length;i++){

        let min = optionItem[i].min
        let max = optionItem[i].max
        let group = optionItem[i].id

        let titleContainer = document.createElement('div');
        titleContainer.className = 'name-price'

        let titleOption = document.createElement('h5');
        titleOption.className = 'option-name'
        titleOption.innerText = optionItem[i].name

        let pillContainer = document.createElement('h5');
        pillContainer.className = 'option-name'

        let pill =  document.createElement('span');
        pill.className = "badge"
        pill.innerText = min + ' Required'

        if(min == 0){

            pill.classList.add("bg-light")
            pill.classList.add("text-muted")
        }
        else {
            pill.classList.add("bg-primary")
        }

        let desOption = document.createElement('small');
        desOption.className = 'text-muted option-des'
        desOption.innerText = optionItem[i].description

        pillContainer.appendChild(pill)
        titleContainer.appendChild(titleOption)
        titleContainer.appendChild(pillContainer)

        optionContainer.appendChild(titleContainer)
        optionContainer.appendChild(desOption)

        for(let j=0;j < optionItem[i].itemList.length;j++){

            let eachOptionContainer = document.createElement('div');
            eachOptionContainer.className = 'name-price'

            let form = document.createElement('div');
            form.className = 'form-check option-div'

            optionPriceLabel = optionItem[i].itemList[j].price

            let input = document.createElement('input');
            input.className = 'form-check-input'
            input.setAttribute('type','checkbox')
            input.id = optionItem[i].itemList[j].id
            input.setAttribute('name',group)
            input.setAttribute('value',optionPriceLabel)

            let label = document.createElement('label');
            label.className = 'form-check-label'
            label.innerText = optionItem[i].itemList[j].name
            label.setAttribute('for',optionItem[i].itemList[j].id)

            let price = document.createElement('p');
            price.className = 'text-muted mb-0'
            if(optionPriceLabel > 0){
                price.innerText = '+฿ ' + optionPriceLabel
            }

            input.onchange = function (){limitCheck(max,min,group)}

            if(min == 1 && max == 1){
                input.setAttribute('type','radio')
                input.onchange = function (){radioCheck(group)}
            }

            form.appendChild(input)
            form.appendChild(label)
            eachOptionContainer.appendChild(form)
            eachOptionContainer.appendChild(price)
            optionContainer.appendChild(eachOptionContainer)
        }
        $('#modalBody').append(optionContainer)

        if(min <= 0){
            groupName[group] = true
        }
        else {
            groupName[group] = false
        }
    }
    buttonCheck()
    calculatePrice()
}

function radioCheck(name){
    if(groupName[name] == false){
        groupName[name] = true
        buttonCheck()
    }
    calculatePrice()
}

function limitCheck(max,min,name){
    optionPrice = 0
    let target = "input[name='"+name+"']"
    // console.log($(target+":checked").length)
    if (($(target+":checked").length >= max)){
        $(target).not(":checked").prop("disabled", true)
    }
    else {
        $(target).prop("disabled",false)
    }

    if(($(target+":checked").length >= min)){
        groupName[name] = true
        buttonCheck()
    }
    else{
        groupName[name] = false
        $('#goCart').prop('disabled', true);
    }
    calculatePrice()
}

function buttonCheck(){
    $('#goCart').prop('disabled', false);

    for(var name in groupName){
        if(groupName[name] == false){
            $('#goCart').prop('disabled', true);
        }
    }
    // console.log(groupName)
}

function calculatePrice(){
    optionPrice = 0
    for(var name in groupName){
        let gn = $("input[name='"+name+"']"+":checked")
        for(i=0;i < gn.length;i++){
            optionPrice += Number(gn[i].value)
        }
    }
    totalPrice = (startPrice+optionPrice) * ($('#inputQuantity').val())
    $('#priceButton').text(totalPrice)
}

function createOrderRequest(){
    $('#staticBackdrop').modal('hide')

    let allOptionGroup = []
    let optionText = []
    for(let name in groupName){
        let optional
        let gn = $("input[name='"+name+"']"+":checked")
        let selectedOption = []
        for(j=0;j < gn.length ;j++){
            let optionId = gn[j].id
            selectedOption.push(optionId)
            optionText.push($("[for="+optionId+"]")[0].innerText)
        }
        optional = {
            "optionalId": name,
            "itemOptionalId": selectedOption
        }
        if(selectedOption.length > 0 ){
            allOptionGroup.push(optional)
        }
    }
    let order ={
        "itemId": idItem,
        "quantity": $('#inputQuantity').val(),
        "comment": $('#inputComment').val(),
        "selectItems": allOptionGroup
    }
    let text ={
        "name":  $('#titleItem').text(),
        "price": totalPrice,
        "opText": optionText
    }

    return [order,text]
}

function addCart(){
    $('footer').show()

    let request = createOrderRequest()
    orderRequests.push(request[0])
    cartText.push(request[1])
    console.log(orderRequests)
    console.log(cartText)
}



$(document).on('click', '.number-spinner button', function () {
    var btn = $(this),
        oldValue = btn.closest('.number-spinner').find('input').val().trim(),
        newVal = oldValue;

    if (btn.attr('data-dir') == 'up') {
        if(oldValue < maxQty){
            newVal = parseInt(oldValue) + 1;
        }
    } else {
        if (oldValue > 1) {
            newVal = parseInt(oldValue) - 1;
        } else {
            newVal = 1;
        }
    }
    btn.closest('.number-spinner').find('input').val(newVal);
    calculatePrice()
});


function createCartItem(){
    let total = 0

    $('#cartModalBody').empty()

    if(cartText.length > 0){
        $('#cartBtn').prop('disabled',false)
    }
    else {
        $('#cartBtn').prop('disabled',true)
        $('footer').hide()
    }

    for(let i=0;i<cartText.length;i++) {
        let index = i

        let container = document.createElement('div')
        container.className = 'my-2'

        let title = document.createElement('div')
        title.className = 'name-price'
        title.setAttribute('data-bs-toggle','modal')
        title.setAttribute('data-bs-target',"#staticBackdrop")
        title.onclick = async function(){await editItem(index)}

        let itemName = document.createElement('h6')
        itemName.className = 'menu-name'

        let price = document.createElement('h6')

        let option = document.createElement('small')
        option.className = 'text-muted'

        let comment = document.createElement('small')
        comment.className = 'text-muted'

        let linkOpContainer = document.createElement('div')
        linkOpContainer.className = 'name-price'

        let linkDelete = document.createElement('button')
        linkDelete.className = 'btn btn-delete-cart'
        linkDelete.setAttribute('type', 'button')
        linkDelete.onclick = function (){deleteItem(index)}

        // let editLink = document.createElement('a')
        // editLink.className = 'stretched-link'


        let iconTrash = document.createElement('i')
        iconTrash.className = 'bi bi-trash-fill'

        let opCommentContainer = document.createElement('div')
        opCommentContainer.className = 'columnFlex'

        itemName.innerText = orderRequests[i].quantity+'x '+cartText[i].name
        comment.innerText = orderRequests[i].comment
        price.innerText = '฿ '+cartText[i].price
        option.innerText = cartText[i].opText.join(', ')

        total += Number(cartText[i].price)


        linkDelete.appendChild(iconTrash)
        title.appendChild(itemName)
        title.appendChild(price)
        container.appendChild(title)
        opCommentContainer.appendChild(option)
        opCommentContainer.appendChild(comment)
        linkOpContainer.appendChild(opCommentContainer)
        linkOpContainer.appendChild(linkDelete)
        container.appendChild(linkOpContainer)

        $('#cartModalBody').append(container)
    }
    $('#cartBtnText').text('฿ '+total)
}

async function editItem(index){
    // console.log(index)
    let id = orderRequests[index].itemId
    await createModal(id)
    $('#inputQuantity').val(orderRequests[index].quantity)
    $('#inputComment').val(orderRequests[index].comment)

    let select = orderRequests[index].selectItems
    for(let i=0;i<select.length;i++){
        for(let j=0;j<select[i].itemOptionalId.length;j++){
            document.getElementById(select[i].itemOptionalId[j]).setAttribute('checked',true)
        }
        document.getElementById(select[i].itemOptionalId[0]).onchange()
    }

    $('#nameButton').text('Edit Cart')
    document.getElementById('closeModal').onclick = function (){$('#cartModal').modal('show')}
    document.getElementById('goCart').onclick = function (){editRequestList(index)}
}

function editRequestList(index){
   let request = createOrderRequest()
    orderRequests[index] = request[0]
    cartText[index] = request[1]
    openCartModal()
    // console.log(orderRequests)
    // console.log(cartText)
}

function openCartModal(){
    createCartItem()
    $('#cartModal').modal('show')
}

function deleteItem(index){
    // console.log(index)

    orderRequests.splice(index,1)
    cartText.splice(index,1)
    createCartItem()
    $('#cartModal').modal('show')

}

function order(){
    let json = {
        "orderRequests": orderRequests
    }

    $.ajax({
        url: '/api/order/new',
        data: JSON.stringify(json) ,
        contentType: "application/json" ,
        type: 'POST',
        success: function () {
            console.log('success')
            location.href = '/order'
        }
    });
}

async function createBillItem(){
    $('#BillModalBody').empty()

    // let json = {
    //     "billId": 19,
    //     "person": 2,
    //     "type": "DINE-IN",
    //     "timestamp": "2022-03-17T22:57:31",
    //     "orders": [
    //         {
    //             "id": 62,
    //             "name": "ชานม",
    //             "option": "หวาน 100%, ไข่มุกน้ำผึ้ง, ครัมเบิ้ลวานิลลา",
    //             "quantity": 1,
    //             "price": 75.0,
    //             "amount": 75.0,
    //             "comment": "venom",
    //             "status": "ORDER"
    //         }
    //     ],
    //     "subTotal": 75.0
    // }

    let json = await getBillData()

    let orders = json.orders

    if(json.orders != null){
    if(orders.length > 0 ){
        $('#billBtn').prop('disabled',false)
    }
    else {
        $('#billBtn').prop('disabled',true)
    }

    const template = `${orders.map(order => `
  <div>
    <div class="name-price">
        <h6 class="menu-name"> ${order.quantity}x ${order.name}</h6>
        <h6>฿ ${order.amount}</h6>
    </div>
    <div class="name-price-status">
        <div class="columnFlex">
            <small class="text-muted">${order.option}</small>
            <br>
            <small class="text-muted">${order.comment}</small>
        </div>
        <span class="badge rounded-pill ${order.status}">${order.status}</span>
     </div>
  </div>
        `).join('')}
`;
    $('#BillModalBody').append(template)
    $('#billBtnText').text('฿ '+json.subTotal)
    }
}