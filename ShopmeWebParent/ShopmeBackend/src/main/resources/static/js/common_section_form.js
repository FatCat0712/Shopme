function loadCategories() {
    $.get(categoriesModuleURL + '/list_categories_in_form', function(responseJSON) {
          dropDownCategories.empty();
           $.each(responseJSON, function(index, category) {
                $("<option>").val(category.id + '|' + category.name).text(category.name).appendTo(dropDownCategories);
           });
    })
}

function loadArticles() {
     $.get(articlesModuleURL + '/list_articles', function(responseJSON) {
              dropDownArticles.empty();
               $.each(responseJSON, function(index, article) {
                    $("<option>").val(article.id + '|' + article.title).text(article.title).appendTo(dropDownArticles);
               });
        })
}

function loadBrands() {
    $.get(brandsModuleURL + '/list_brands', function(responseJSON) {
          dropDownBrands.empty();
           $.each(responseJSON, function(index, brand) {
                $("<option>").val(brand.id + '|' + brand.name).text(brand.name).appendTo(dropDownBrands);
           });
    })
}

function addChosenItem(value, dropDownChosenItems) {
       let itemId = value.split('|')[0];
       let itemName = value.split('|')[1].replaceAll('-','');
       $("<option>").val(itemId).text(itemName).appendTo(dropDownChosenItems);
}

function moveUp() {
        const selectedIndex = dropDownChosenDOM.selectedIndex;
        const selectedItem = dropDownChosenDOM.options[selectedIndex];
        const previousIndex = selectedIndex - 1;
        if(previousIndex > -1) {
           const previousItem = dropDownChosenDOM.options[previousIndex];
            dropDownChosenDOM.insertBefore(selectedItem, previousItem);
        }
}

function moveDown() {
    let currentLength = dropDownChosenDOM.options.length;
    const selectedIndex = dropDownChosenDOM.selectedIndex;
    const selectedItem = dropDownChosenDOM.options[selectedIndex];
    const nextIndex = selectedIndex + 1;
    if(nextIndex < currentLength){
        const nextItem =   dropDownChosenDOM.options[nextIndex];
        dropDownChosenDOM.insertBefore(selectedItem, nextItem.nextSibling);
    }
}

function removeChosenItem() {
     let dropDownChosenItems;
    if(label === 'category') {
        dropDownChosenItems = dropDownChosenCategories;
    }
    else if(label === 'article') {
        dropDownChosenItems = dropDownChosenArticles;
    }
    else if(label === 'brand') {
        dropDownChosenItems = dropDownChosenBrands;
    }
    let optionValue = dropDownChosenItems.val();
    let dropDownId = dropDownChosenItems.attr('id');
    $("#" + dropDownId  +" option[value=" + optionValue  + "]").remove();
}

