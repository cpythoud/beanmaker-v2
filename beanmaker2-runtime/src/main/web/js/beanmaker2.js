class Beanmaker2 {

    static VERSION = 'v0.1.2 -- 2022-10-07';

    static DEFAULT_PARAMETERS = {
        // * config
        submitElement: 'button[type="submit"]',
        loadingStatusElement: 'span.loading',
        servletURL: undefined,
        submitSuccessFunction: undefined,
        submitSuccessURL: undefined,
        noSessionURL: '/',
        errorFunction: undefined,
        errorContainerIDPrefix: 'error_messages_',
        errorContainerSelector: undefined,
        elementToScrollUpOnError: 'body',
        deleteLinkClass: undefined,
        deleteSuccessFunction: undefined,
        deleteSuccessURL: undefined,
        deleteErrorFunction: undefined,
        deleteConfirmationText: 'Delete?',
        deleteConfirmationFunction: (id, message) => {
            return confirm(message);
        },
        // * CSS classes
        loadingClass: '',
        errorContainerStyles: '',
        errorContainerULStyles: '',
        errorContainerLIStyles: '',
        fieldInErrorStyles: '',
    }

    static isStringEmpty(str) {
        return str.trim().length === 0;
    }

    static parseID(idString) {
        const parts = idString.split('_');
        return parts[parts.length - 1];
    }

    static getBeanID(element) {
        return Beanmaker2.parseID(element.getAttribute('id'));
    }

    static addClasses(element, classes) {
        const cssClasses = classes.trim().split(/\s+/);
        for (const cssClass of cssClasses)
            element.classList.add(cssClass);
    }

    static removeClasses(element, classes) {
        const cssClasses = classes.trim().split(/\s+/);
        for (const cssClass of cssClasses)
            element.classList.remove(cssClass);
    }

    static uncapitalize(str) {
        return str.charAt(0).toLowerCase() + str.slice(1);
    }

    constructor(bean, nonDefaultParameters) {
        console.log("Beanmaker2 -- " + bean + " -- " + Beanmaker2.VERSION);
        this.bean = bean;
        this.parameters = Object.assign(Beanmaker2.DEFAULT_PARAMETERS, nonDefaultParameters);

        if (!this.parameters.deleteLinkClass)
            this.parameters.deleteLinkClass = 'delete_' + Beanmaker2.uncapitalize(bean);

        this.createEventListeners();
    }

    createEventListeners() {
        this.addSubmitOperation();
        this.addDeleteOperation();
    }

    addSubmitOperation() {
        document.addEventListener('submit', event => {
            const $form = event.target;
            if ($form.getAttribute('name') === this.bean) {
                event.preventDefault();
                const multipart = $form.getAttribute('enctype') === 'multipart/form-data';
                this.showLoadingStatus($form);
                fetch(this.getSubmitFormAction($form), this.getSubmitFormParameters($form, multipart))
                    .then(response => {
                        if (response.ok && response.headers.get("Content-Type") === "text/json; charset=UTF-8")
                            return response.json();

                        console.log(response.text());

                        throw new Error(`Unexpected response status ${response.status} or content type`);
                    })
                    .then(data => {
                        this.removeErrorMarking($form);
                        switch (data.status) {
                            case 'ok':
                                if (this.parameters.submitSuccessFunction)
                                    this.parameters.submitSuccessFunction($form, data);
                                else if (this.parameters.submitSuccessURL)
                                    window.location.href = this.parameters.submitSuccessURL;
                                else
                                    window.location.reload();
                                break;
                            case 'no session':
                                window.location.href = this.parameters.noSessionURL;
                                break;
                            case 'errors':
                                if (this.parameters.errorFunction === undefined) {
                                    this.showErrorMessages($form, data.errors);
                                    if (this.parameters.elementToScrollUpOnError) {
                                        if (this.parameters.elementToScrollUpOnError === 'body')
                                            window.scrollTo(0, 0);
                                        else
                                            document.querySelector(this.parameters.elementToScrollUpOnError).scrollIntoView();
                                    }
                                } else {
                                    this.parameters.errorFunction();
                                }
                                break;
                            default:
                                // TODO: improve error reporting
                                console.log(data.status);
                                alert('An unexpected error has occurred. See console output for more information.');
                        }
                        this.removeLoadingStatus($form);
                    })
                    .catch(error => {
                        // TODO: improve error reporting
                        console.log(error);
                        alert('An unexpected error has occurred. See console output for more information.');
                        this.removeLoadingStatus($form);
                    });
            }
        })
    }

    showLoadingStatus($form) {
        if (this.parameters.loadingClass)
            Beanmaker2.addClasses($form.querySelector(this.parameters.loadingStatusElement), this.parameters.loadingClass);
        $form.querySelector(this.parameters.submitElement).setAttribute('disabled', 'disabled');
    }

    removeLoadingStatus($form) {
        if (this.parameters.loadingClass)
            Beanmaker2.removeClasses($form.querySelector(this.parameters.loadingStatusElement), this.parameters.loadingClass);
        $form.querySelector(this.parameters.submitElement).removeAttribute('disabled');
    }

    removeErrorMarking($form) {
        if (!Beanmaker2.isStringEmpty(this.parameters.errorContainerStyles))
            Beanmaker2.removeClasses(this.getErrorContainer($form), this.parameters.errorContainerStyles);

        if (!Beanmaker2.isStringEmpty(this.parameters.fieldInErrorStyles))
            for (const child of $form.children)
                Beanmaker2.removeClasses(child, this.parameters.fieldInErrorStyles);
    }

    getSubmitFormAction($form) {
        if (this.parameters.servletURL)
            return this.parameters.servletURL;

        return $form.getAttribute('action');
    }

    getSubmitFormParameters($form, multipart) {
        const parameters = {
            method: 'POST',
            cache: 'no-store',
            credentials: 'same-origin'
        };

        const formData = new FormData($form);
        formData.set('beanmaker_operation', 'submit');

        if (multipart)
            parameters.body = formData;
        else
            parameters.body = new URLSearchParams(formData);

        return parameters;
    }

    showErrorMessages($form, errors) {
        const $container = this.getErrorContainer($form);

        $container.replaceChildren();
        if (!Beanmaker2.isStringEmpty(this.parameters.errorContainerStyles))
            Beanmaker2.addClasses($container, this.parameters.errorContainerStyles);

        const errorList = document.createElement('ul');
        if (!Beanmaker2.isStringEmpty(this.parameters.errorContainerULStyles))
            Beanmaker2.addClasses(errorList, this.parameters.errorContainerULStyles);
        const errorCount = errors.length;
        for (let i = 0; i < errorCount; i++) {
            const errorElement = document.createElement('li');
            if (!Beanmaker2.isStringEmpty(this.parameters.errorContainerLIStyles))
                Beanmaker2.addClasses(errorElement, this.parameters.errorContainerLIStyles);
            if (errors[i].fieldLabel === '__GLOBAL__')
                errorElement.innerText = errors[i].message;
            else {
                errorElement.innerText = errors[i].fieldLabel + ' : ' + errors[i].message;
                if (!Beanmaker2.isStringEmpty(this.parameters.fieldInErrorStyles)) {
                    //const field = $form.querySelector('name[' + errorList.fieldName + ']');
                    const field = $form[errors[i].fieldName];
                    if (field)
                        Beanmaker2.addClasses(field, this.parameters.fieldInErrorStyles);
                    else
                        console.log('Could not retrieve field ' + errors[i].fieldName + ' in form');
                }
            }
            errorList.appendChild(errorElement);
        }
        $container.appendChild(errorList);
    }

    getErrorContainer($form) {
        let errorContainer;
        if (this.parameters.errorContainerSelector)
            errorContainer = document.querySelector(this.parameters.errorContainerSelector);
        else
            errorContainer = document.getElementById(this.parameters.errorContainerIDPrefix + Beanmaker2.getBeanID($form));

        if (!errorContainer)
            throw new Error("Could not determine where to display error messages on page");

        return errorContainer;
    }

    addDeleteOperation() {
        document.addEventListener('click', event => {
            const $link = event.target.closest('.' + this.parameters.deleteLinkClass);
            if ($link) {
                event.preventDefault();
                const id = Beanmaker2.getBeanID($link);
                if (this.parameters.deleteConfirmationFunction(id, this.parameters.deleteConfirmationText)) {
                    fetch(this.parameters.servletURL, this.getDeleteParameters(id))
                        .then(response => {
                            if (response.ok && response.headers.get("Content-Type") === "text/json; charset=UTF-8")
                                return response.json();

                            console.log(response.text());

                            throw new Error(`Unexpected response status ${response.status} or content type`);
                        })
                        .then(data => {
                            switch (data.status) {
                                case 'ok':
                                    if (this.parameters.deleteSuccessFunction)
                                        this.parameters.deleteSuccessFunction(id, data);
                                    else if (this.parameters.deleteSuccessURL)
                                        window.location.href = this.parameters.deleteSuccessURL;
                                    else
                                        window.location.reload();
                                    break;
                                case 'no session':
                                    window.location.href = this.parameters.noSessionURL;
                                    break;
                                case 'errors':
                                    if (this.parameters.deleteErrorFunction)
                                        this.parameters.deleteErrorFunction(id, data);
                                    break;
                                default:
                                    // TODO: improve error reporting
                                    console.log(data.status);
                                    alert('An unexpected error has occurred. See console output for more information.');
                            }
                        })
                        .catch(error => {
                            // TODO: improve error reporting
                            console.log(error);
                            alert('An unexpected error has occurred. See console output for more information.');
                        });
                }
            }
        });
    }

    getDeleteParameters(id) {
        const parameters = {
            method: 'POST',
            cache: 'no-store',
            credentials: 'same-origin'
        };

        const formData = new FormData();
        formData.set('beanmaker_operation', 'delete');
        formData.set('id', id.toString());

        parameters.body = new URLSearchParams(formData);

        return parameters;
    }

}
