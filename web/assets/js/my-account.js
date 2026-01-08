var modelList;


async function loadFeatures() {
    const response = await fetch(
            "LoadFeatures"
            );

    if (response.ok) {
        const json = await response.json();

        const categoryList = json.categoryList;
        modelList = json.modelList;
        const colorList = json.colorList;
        const conditionList = json.conditionList;

        loadSelect("categorySelect", categoryList, "name");
        //loadSelect("modelSelect", modelList, "name");
        loadSelect("colorSelect", colorList, "name");
        loadSelect("conditionSelect", conditionList, "name");    
    }
}


function loadSelect(selectTagId, specificList, property) {
    const selectTag = document.getElementById(selectTagId);
    
    specificList.forEach( itemObjectFromList =>{
        let optionTag = document.createElement("option");
        optionTag.value = itemObjectFromList.id;
        optionTag.innerHTML = itemObjectFromList[property];
        selectTag.appendChild(optionTag);
    });
}


function updateModelsByCategory() {
    let modelSelectTag = document.getElementById("modelSelect");
    modelSelectTag.length = 1;      //select tag's option length ("1" to only keep 1st option)

    let categoryId = document.getElementById("categorySelect").value;

    modelList.forEach( modelListItemObject =>{
        if (modelListItemObject.category.id == categoryId) {
            let optionTag = document.createElement("option");
            optionTag.value = modelListItemObject.id;
            optionTag.innerHTML = modelListItemObject.name;
            modelSelectTag.appendChild(optionTag);
        }
    });
}


async function listAProduct() {
    const categorySelectTag = document.getElementById("categorySelect");
    const modelSelectTag = document.getElementById("modelSelect");
    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const colorSelectTag = document.getElementById("colorSelect");
    const conditionSelectTag = document.getElementById("conditionSelect");
    const priceTag = document.getElementById("price");
    const quantityTag = document.getElementById("quantity");
    const image1Tag = document.getElementById("img1");
    const image2Tag = document.getElementById("img2");
    const image3Tag = document.getElementById("img3");

    const formData = new FormData();
    formData.append("categoryId", categorySelectTag.value);
    formData.append("modelId", modelSelectTag.value);
    formData.append("title", titleTag.value);
    formData.append("description", descriptionTag.value);
    formData.append("colorId", colorSelectTag.value);
    formData.append("conditionId", conditionSelectTag.value);
    formData.append("price", priceTag.value);
    formData.append("quantity", quantityTag.value);
    formData.append("image1", image1Tag.files[0]);
    formData.append("image2", image2Tag.files[0]);
    formData.append("image3", image3Tag.files[0]);

    const response = await fetch(
            "ListAProduct",
            {
                method: "POST",
                body: formData
            }
    );
    
    const popup = Notification();
    
    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            categorySelectTag.value = 0;
            colorSelectTag.value = 0;
            conditionSelectTag.value = 0;
            modelSelectTag.length = 1;
            titleTag.value = "";
            descriptionTag.value = "";
            priceTag.value = "";
            quantityTag.value = 1;
            image1Tag.value = null;
            image2Tag.value = null;
            image3Tag.value = null;
            
            popup.success({
                message: json.content
            });
        } else {
            popup.error({
                message: json.content
            });
        }

    } else {
        popup.error({
            message: 'Please try again!'
        });
    }
}