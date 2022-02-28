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

function getCategoryData(){
    return new Promise(function (resolve, reject){
        $.get( '/api/item/category', function( data ) {
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

$( document ).ready(async function() {
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

    let categoryName = document.createElement('h1')
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
    navItem.className = 'nav-link'

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

function getOptionData(id){
    return new Promise( function (resolve, reject) {
        $.get( '/api/item/optional-of-id/' + id , function( data ) {
            resolve(data)
        });
    })
}

async function createModal(id){
    idItem = id
    // console.log(idItem)
    allItem = await getItemData(id)
    startPrice = allItem.price
        // console.log(allItem)
    $('#titleItem').text(allItem.name)
    $('#desItem').text(allItem.description)
    $('#priceItem').text('฿ ' + startPrice)
    $('#priceButton').text(startPrice)
    $('#modalBody').empty()
    let optionData = await getOptionData(id)
    createOption(optionData)
}


function createOption(optionItem){

    groupName = {}

    // console.log(optionItem)
    let optionContainer = document.createElement('div');

    for(let i=0;i < optionItem.length;i++){

        let titleOption = document.createElement('h5');
        titleOption.className = 'option-name'
        titleOption.innerText = optionItem[i].name

        let desOption = document.createElement('small');
        desOption.className = 'text-muted option-des'
        desOption.innerText = optionItem[i].description

        optionContainer.appendChild(titleOption)
        optionContainer.appendChild(desOption)

        let min = optionItem[i].min
        let max = optionItem[i].max
        let group = optionItem[i].id

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
    console.log(groupName)
}

function calculatePrice(){
    optionPrice = 0
    for(var name in groupName){
        let gn = $("input[name='"+name+"']"+":checked")
        for(i=0;i < gn.length;i++){
            optionPrice += Number(gn[i].value)
        }
    }
    totalPrice = startPrice+optionPrice
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
    let request = createOrderRequest()
    orderRequests.push(request[0])
    cartText.push(request[1])
    console.log(orderRequests)
    console.log(cartText)
}



$(document).on('click', '.number-spinner button', function () {
    var btn = $(this),
        oldValue = btn.closest('.number-spinner').find('input').val().trim(),
        newVal = 0;

    if (btn.attr('data-dir') == 'up') {
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


function createCartItem(order){
    $('#cartModalBody').empty()

    for(let i=0;i<cartText.length;i++) {
        let index = i

        let container = document.createElement('div')

        let title = document.createElement('div')
        title.className = 'name-price'

        let itemName = document.createElement('h6')
        itemName.className = 'menu-name'

        let price = document.createElement('h6')

        let option = document.createElement('small')
        option.className = 'text-muted'

        let linkContainer = document.createElement('div')

        let link = document.createElement('a')
        link.setAttribute('type', 'button')
        link.innerText = 'Edit'
        link.setAttribute('data-bs-toggle','modal')
        link.setAttribute('data-bs-target',"#staticBackdrop")
        link.onclick = function(){editItem(index)}

        itemName.innerText = cartText[i].name
        price.innerText = cartText[i].price
        option.innerText = cartText[i].opText.join(', ')

        title.appendChild(itemName)
        title.appendChild(price)
        container.appendChild(title)
        container.appendChild(option)
        linkContainer.appendChild(link)
        container.appendChild(linkContainer)
        $('#cartModalBody').append(container)
    }
}

async function editItem(index){
    let id = orderRequests[index].itemId
    await createModal(id)

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
    console.log(orderRequests)
    console.log(cartText)
}

function openCartModal(){
    createCartItem()
    $('#cartModal').modal('show')
}