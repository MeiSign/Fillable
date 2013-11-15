var settings, FillableWidget = {
    settings: {
        values: {}
    },

    init: function(inputFieldId, fblHost, fblIndex) {
        settings = this.settings;
        settings.inputField = document.getElementById(inputFieldId);
        settings.form = this.getFormFrom();
        settings.host = fblHost;
        settings.index = fblIndex;
        this.createUi();
        this.bindUiActions();
    },

    createUi: function() {
        this.createOptionBox();
        settings.inputField.setAttribute("autocomplete", "off");
    },

    bindUiActions: function() {
        settings.form.setAttribute("onsubmit", "FillableWidget.submitForm()");
        settings.inputField.onkeyup = function() {
            FillableWidget.changeInput();
        }

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
            optionBox.setAttribute("class", "fblOptionbox fblHidden");
            var rect = settings.inputField.getBoundingClientRect();
            optionBox.style.left = rect.left + "px";
            optionBox.style.top = rect.bottom + "px";
            optionBox.style.minWidth = settings.inputField.offsetWidth + "px"
        }

        var optionBox = document.createElement("ul");
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
            this.getOptions();
        }
    },

    submitForm: function() {
        alert("Select")
    },

    getOptions: function() {
        var http = getHttpRequestObject();
        if (http != null) {
            http.open("GET", getOptionsUrl(), true);
            http.send();
            http.onreadystatechange = function() {
                if ((http.readyState == 4) && (http.status == 200)) {
                    emptyOptionList();
                    var json = eval("("+http.responseText+")")
                    for (var i = 0; i < json.options.length; i++) {
                        var newLi = document.createElement("li");
                        var text = document.createTextNode(json.options[i].text);
                        newLi.appendChild(text);
                        settings.optionBox.appendChild(newLi)
                    }
                }
            }
        }

        function getHttpRequestObject() {
            if (window.XMLHttpRequest) {
                return new XMLHttpRequest();
            } else if (window.ActiveXObject) {
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
        }

        function getOptionsUrl() {
            return settings.host + "/complete/" + settings.index + "/" + settings.inputField.value;
        }

        function emptyOptionList() {
            while (settings.optionBox.firstChild) {
                settings.optionBox.removeChild(settings.optionBox.firstChild);
            }
        }
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