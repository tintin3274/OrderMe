let item
let i,j
let allItem

$.get( '/api/item/category', function( data ) {
    // console.log(data)
    for(i=0;i<data.length;i++){
        let category = data[i]
        createNavCategory(category)
        $.get( '/api/item/category/'+ category, function( data ) {
            // console.log(category )
            item = data
            console.log(item)
            createCard(category)
        });
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

    for(i=0;i<item.length;i++){
        if(item[i].display == 1){
            let card = document.createElement('div');
            card.className = 'col-md-6 card d-flex';

            let detail = document.createElement('div');
            detail.className ='row my-1'

            let pictureFrame =document.createElement('div');
            pictureFrame.className = 'columnImage col-auto'

            var picture = document.createElement('img');
            picture.className = 'picture';
            picture.id = 'img'+ item[i].id
            if (item[i].image != null){
                console.log(item[i].image)
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
            prices.innerText = item[i].price + ' บาท'

            let link = document.createElement('a');
            link.className = 'stretched-link'
            link.setAttribute('data-bs-toggle','modal')
            link.setAttribute('data-bs-target',"#staticBackdrop")
            link.id = item[i].id
            link.onclick = function(){createModal(link.id)}

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

function createModal(id){
    $.get( '/api/item/' + id , function( data ) {
        allItem = data
        $('#titleItem').text(allItem.name)
        $('#desItem').text(allItem.description)
        $.get( '/api/item/optional-of-id/' + id , function( data ) {
            $('#modalBody').empty()
            createOption(data)
        });

    });
}

function createOption(optionItem){
    console.log(optionItem)
    let optionContainer = document.createElement('div');

    for(i=0;i < optionItem.length;i++){

        let titleOption = document.createElement('h5');
        titleOption.className = 'menu-name'
        titleOption.innerText = optionItem[i].name

        let desOption = document.createElement('small');
        desOption.className = 'text-muted'
        desOption.innerText = optionItem[i].description

        optionContainer.appendChild(titleOption)
        optionContainer.appendChild(desOption)

        let min = optionItem[i].min
        let max = optionItem[i].max

        for(j=0;j < optionItem[i].itemList.length;j++){

            let form = document.createElement('div');
            form.className = 'form-check'

            let input = document.createElement('input');
            input.className = 'form-check-input'
            input.setAttribute('type','checkbox')
            input.id = optionItem[i].itemList[j].name
            input.setAttribute('name',optionItem[i].name)

            let label = document.createElement('label');
            label.className = 'form-check-label'
            label.innerText = optionItem[i].itemList[j].name
            label.setAttribute('for',optionItem[i].itemList[j].name)

            if(min == 1 && max == 1){
                input.setAttribute('type','radio')
            }
            else {
                input.onchange = function (){limitCheck(max,min,input.name)}
            }

            form.appendChild(input)
            form.appendChild(label)
            optionContainer.appendChild(form)
        }
        $('#modalBody').append(optionContainer)
    }
}

function limitCheck(max,min,name){
    let target = "input[name='"+name+"']"
    // console.log($(target+":checked").length)
    if(($(target+":checked").length >= max)){
        $(target).not(":checked").prop("disabled", true)
    }
    if(($(target+":checked").length < max)){
        $(target).prop("disabled",false)
    }
}
