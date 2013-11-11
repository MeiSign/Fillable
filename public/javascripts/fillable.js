var FillableWidget = {
    settings: {
    },

    init: function(inputFieldId) {
        this.settings.inputField = document.getElementById(inputFieldId);
        this.settings.form = this.getFormFrom();
        this.bindUiActions();
    },

    bindUiActions: function() {
        this.settings.form.setAttribute("onsubmit", "FillableWidget.submitForm()")
    },

    getFormFrom: function() {
        return this.settings.inputField.form;
    },

    submitForm: function() {
        alert("Select")
    }
}