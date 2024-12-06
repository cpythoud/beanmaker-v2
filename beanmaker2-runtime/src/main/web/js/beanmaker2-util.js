// * Beanmaker2Util - V0.2 - 2024-12-06

class Beanmaker2Util {
    constructor() { }

    setupDataRefresh(id, availabilityCheckURL, dataURL, checkFrequency = 5) {

        const recursiveCheck = () => {
            fetch(availabilityCheckURL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.available) {
                        fetch(dataURL)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById(id).innerHTML = html;
                            })
                            .catch(error => console.error('Error loading data URL:', error));
                    } else {
                        window.setTimeout(recursiveCheck, checkFrequency * 1000);
                    }
                })
                .catch(error => console.error('Error checking availability:', error));
        }

        window.setTimeout(recursiveCheck, checkFrequency * 1000);
    }

    setupJavaScriptRefresh(availabilityCheckURL, dataURL, checkFrequency = 5) {

        const recursiveCheck = () => {
            fetch(availabilityCheckURL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.available) {
                        fetch(dataURL)
                            .then(response => response.text())
                            .then(jsCode => {
                                const scriptElement = document.createElement('script');
                                scriptElement.innerHTML = jsCode;
                                document.body.appendChild(scriptElement);
                            })
                            .catch(error => console.error('Error loading data URL:', error));
                    } else {
                        window.setTimeout(recursiveCheck, checkFrequency * 1000);
                    }
                })
                .catch(error => console.error('Error checking availability:', error));
        }

        window.setTimeout(recursiveCheck, checkFrequency * 1000);
    }

    static showContent(containerElementId, loadingIndicatorElementId, htmlContent) {
        if (htmlContent === null || htmlContent === undefined) {
            throw new Error('HTML content must not be null or undefined.');
        }

        const containerElement = document.getElementById(containerElementId);
        const loadingIndicatorElement = document.getElementById(loadingIndicatorElementId);

        if (!containerElement) {
            throw new Error(`Element with id "${containerElementId}" not found.`);
        }
        if (!loadingIndicatorElement) {
            throw new Error(`Element with id "${loadingIndicatorElementId}" not found.`);
        }

        containerElement.innerHTML = htmlContent;
        containerElement.classList.remove('beanmaker_removed');
        loadingIndicatorElement.classList.add('beanmaker_removed');
    }

}
