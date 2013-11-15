var settings, FillableWidget = {
    settings: {
        values: {}
    },

    init: function(inputFieldId) {
        settings = this.settings;
        settings.inputField = document.getElementById(inputFieldId);
        settings.inputField.setAttribute("autocomplete", "off");
        settings.form = this.getFormFrom();
        this.createUi();
        this.bindUiActions();
    },

    createUi: function() {
        this.createOptionBox();
    },

    bindUiActions: function() {
        settings.form.setAttribute("onsubmit", "FillableWidget.submitForm()");
        settings.inputField.onkeyup = function() { FillableWidget.changeInput(); }

        document.querySelector('body').addEventListener('click', function(event) {
            if (event.target.className === 'fblOption') {
                FillableWidget.selectOption(event.target.innerText)
            }
        });
    },

    getFormFrom: function() {
        return settings.inputField.form;
    },

    createOptionBox: function() {
        var styleOptionbox = function(optionBox) {
            optionBox.setAttribute("class", "fblOptionbox");
            var rect = settings.inputField.getBoundingClientRect();
            optionBox.style.left = rect.left + "px";
            optionBox.style.top = rect.bottom + "px";
            optionBox.style.minWidth = settings.inputField.offsetWidth + "px"
        }

        var optionBox = document.createElement("ul");
        optionBox.innerHTML = "<li class='fblOption'>test0 test0 test0 </li><li class='fblOption'>test1</li><li class='fblOption'>test2</li>";
        styleOptionbox(optionBox);
        settings.inputField.parentNode.insertBefore(optionBox, settings.inputField)
        settings.optionBox = optionBox;
    },

    selectOption: function(option) {
        settings.values.chosen = option;
        settings.inputField.value = option;
        settings.inputField.focus();
        FillableHelper.addClass(settings.optionBox, "fblHidden")
    },

    changeInput: function() {
        if (settings.values.typed != settings.inputField.value) {
            settings.values.typed = settings.inputField.value;
            FillableHelper.removeClass(settings.optionBox, "fblHidden");
            console.log(settings.values.typed)
        }
    },

    submitForm: function() {
        alert("Select")
    }
}

var FillableHelper = {
    removeClass: function(element, className) {
        element.className = element.className.replace(new RegExp('(\\s|^)' + className + '(\\s|$)') , '');
    },

    addClass: function(element, className) {
        element.className += " " + className;
    }
}