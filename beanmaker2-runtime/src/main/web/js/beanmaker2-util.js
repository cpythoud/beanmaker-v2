// * Beanmaker2Util - V0.1.1 - 2024-10-17

const Beanmaker2Util = {

    setupDataRefresh: (id, availabilityCheckURL, dataURL, checkFrequency) => {
        if (checkFrequency === undefined)
            checkFrequency = 5;

        window.setTimeout(function () {
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
                        BEANMAKER.setupDataRefresh(id, availabilityCheckURL, dataURL, checkFrequency);
                    }
                })
                .catch(error => console.error('Error checking availability:', error));
        }, checkFrequency * 1000);
    },

    setupJavaScriptRefresh: (availabilityCheckURL, dataURL, checkFrequency) => {
        if (checkFrequency === undefined)
            checkFrequency = 5;

        window.setTimeout(function () {
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
                        Beanmaker2Util.setupJavaScriptRefresh(availabilityCheckURL, dataURL, checkFrequency);
                    }
                })
                .catch(error => console.error('Error checking availability:', error));
        }, checkFrequency * 1000);
    }

};
