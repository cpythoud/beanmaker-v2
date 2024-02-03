// beanmaker.file-upload.js -- v0.2 -- 2024-02-03

BEANMAKER_FILE_UPLOAD_OPTIONS = {
    // * add entry: BEANMAKER_FILE_UPLOAD_OPTIONS.forms.formName = { fieldName1: function1() { ... }, fieldName2: function2() { ... }, ... }
    forms: { },

    getFunction: function (form, field) {
        if (this.forms[form] !== undefined) {
            const checkFunction = this.forms[form][field];
            if (checkFunction !== undefined && typeof checkFunction === 'function') {
                return checkFunction;
            }
        }
        return undefined;
    }
};

$(document).ready(function() {

    const $body = $('body');

    $body.on('change', 'input.file', function () {
        const $this = $(this);
        const formName = $this.closest('form').attr('name');
        const checkFunction = BEANMAKER_FILE_UPLOAD_OPTIONS.getFunction(formName, $this.attr('name'));
        if (checkFunction === undefined || checkFunction()) {
            const id = $(this).attr('id');
            $('#display_' + id)
                .text($(this).val().replace(/C:\\fakepath\\/i, ''));
            $('#remove_' + id).removeClass('hidden');
            $('#delete_' + id).remove();
        }
    });

    $body.on('click', '.remove-file', function () {
        const id = $(this).data('fileinput');
        $('#' + id).val('');
        $('#display_' + id).text('(no file)');
        $(this).addClass("hidden");
        $(this).closest('form')
            .append('<input type="hidden" id="delete_' + id + '" name="delete_' + id.split('_')[0] + '" value="on">');
    });

});
