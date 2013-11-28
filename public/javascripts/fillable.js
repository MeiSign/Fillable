var settings, FillableWidget = {
    settings: {
        values: {
            typed: "",
            chosen: ""
        }
    },

    init: function(options) {
        settings = this.settings;
        settings.inputField = document.getElementById(options.fblInputFieldId);
        settings.form = this.getFormFrom();
        settings.host = options.fblHost;
        settings.index = options.fblIndex;
        this.createUi();
        this.bindUiActions();
    },

    createUi: function() {
        this.createOptionBox();
        settings.inputField.setAttribute("autocomplete", "off");
    },

    bindUiActions: function() {
        settings.inputField.onkeyup = function(event) {
            FillableWidget.changeInput();
        }

        settings.form.addEventListener("submit", function(e) {
            e.preventDefault();
            FillableWidget.addOption();
        });

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

    getOptions: function() {
        var http = FillableHelper.getHttpRequestObject();
        if (http != null) {
            http.open("GET", getOptionsUrl(), true);
            http.send();
            http.onreadystatechange = function() {
                if ((http.readyState == 4) && (http.status == 200)) {
                    emptyOptionList();
                    var options = eval("(" + http.responseText + ")")
                    if (options === undefined || options.length == 0) {
                        FillableHelper.addClass(settings.optionBox, "fblHidden")
                    } else {
                        for (var i = 0; i < options.length; i++) {
                            var newLi = document.createElement("li");
                            newLi.className = "fblOption"
                            var text = options[i].replace(settings.inputField.value, "<span class=\"fblBold\">" + settings.inputField.value + "</span>");
                            newLi.innerHTML = text;
                            settings.optionBox.appendChild(newLi)
                        }
                    }
                }
            }
        }

        function getOptionsUrl() {
            return settings.host + "/complete/" + settings.index + "?toBeCompleted=" + settings.inputField.value;
        }

        function emptyOptionList() {
            while (settings.optionBox.firstChild) {
                settings.optionBox.removeChild(settings.optionBox.firstChild);
            }
        }
    },

    addOption: function () {
        var http = FillableHelper.getHttpRequestObject();
        if (http != null) {
            http.open("POST", addOptionsUrl(), true);
            http.setRequestHeader("Content-type","application/x-www-form-urlencoded");
            http.send("typed=" + encodeURIComponent(settings.values.typed) + "&chosen=" + encodeURIComponent(settings.values.chosen));
        }

        function addOptionsUrl() {
            return settings.host + "/addCompletion/" + settings.index;
        }
    }
}

var FillableHelper = {
    removeClass: function(element, className) {
        element.className = element.className.replace(new RegExp('(\\s|^)' + className + '(\\s|$)') , '');
    },

    addClass: function(element, className) {
        element.className += " " + className;
    },

    getHttpRequestObject: function() {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        }
    }
}