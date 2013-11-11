var settings, FillableWidget = {
    settings: {
        values: {}
    },

    init: function(inputFieldId) {
        settings = this.settings;
        settings.inputField = document.getElementById(inputFieldId);
        settings.inputField.setAttribute("autocomplete", "off");
        settings.form = this.getFormFrom();
        this.bindUiActions();
    },

    bindUiActions: function() {
        settings.form.setAttribute("onsubmit", "FillableWidget.submitForm()");
        settings.inputField.onkeyup = function() { FillableWidget.changeInput(); }
        this.createOptionBox();
    },

    getFormFrom: function() {
        return settings.inputField.form;
    },

    createOptionBox: function() {
        var styleOptionbox = function(optionBox) {
            optionBox.setAttribute("class", "flbOptionbox");
            var rect = settings.inputField.getBoundingClientRect();
            optionBox.style.left = rect.left + "px";
            optionBox.style.top = rect.bottom + "px";
            optionBox.style.minWidth = settings.inputField.offsetWidth + "px"
        }

        var optionBox = document.createElement("ul");
        optionBox.innerHTML = "<li class='flbOption' onClick='FillableWidget.selectOption(this.innerText)'>test0</li><li class='flbOption' onClick='FillableWidget.selectOption(this.innerText)'>test1</li><li class='flbOption' onClick='FillableWidget.selectOption(this.innerText)'>test2</li>";
        styleOptionbox(optionBox);
        settings.inputField.parentNode.insertBefore(optionBox, settings.inputField)
        settings.optionBox = optionBox;
    },

    selectOption: function(option) {
        settings.values.chosen = option;
        settings.inputField.value = option;
        settings.inputField.focus();
        settings.optionBox.className += " flbHidden";
    },

    changeInput: function() {
        if (settings.values.typed != settings.inputField.value) {
            settings.values.typed = settings.inputField.value;
            settings.optionBox.className = settings.optionBox.className.replace(/(?:^|\s)flbHidden(?!\S)/g , '');
            console.log(settings.values.typed)
        }
    },

    submitForm: function() {
        alert("Select")
    }
}