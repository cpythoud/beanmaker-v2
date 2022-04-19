let CCTable2 = (function () {

    'use strict';

    function createEventListeners(instance) {
        console.log("CCTABLE2: build #60");

        // * FILTERING *

        instance._table.querySelectorAll('input.' + instance._settings.formElementFilterCssClass).forEach(function (filterField) {
            filterField.addEventListener('keyup', function() {
                filter(instance);
            });
        });

        instance._table.querySelectorAll('input.' + instance._settings.formElementFilterCssClass).forEach(function (filterField) {
            filterField.addEventListener('dblclick', function () {
                instance._settings.showAdvancedSearchModale(instance._table.id, filterField.name);
            });
        });

        document.addEventListener('submit', function (event) {
            if (event.target && event.target.name === instance._settings.advancedSearchFormName) {
                event.preventDefault();
                advancedSearch(instance);
            }
        });

        instance._table.querySelectorAll('select.' + instance._settings.formElementFilterCssClass).forEach(function (filterFileld) {
            filterFileld.addEventListener('change', function() {
                filter(instance);
            });
        });

        instance._table.querySelector('a.' + instance._settings.removeFilteringLinkCssClass).addEventListener('click', function (event) {
            event.preventDefault();
            clearFilters(instance);
        });

        readCookies(instance);  // * Will also produce updateSums() & zebra() calls


        // * SORTING *

        instance._table.querySelectorAll('th.' + instance._settings.thSortableTitleCssClass).forEach(function (titleCell) {
            titleCell.addEventListener('click', function () {
                sort(instance, titleCell.dataset.sortClass);
            });
        });


        // * COLUMNS MASKING *

        if (tableDoesMasking(instance)) {
            showOrHideColumns(instance);

            instance._table.querySelector('a.' + instance._settings.maskingLinkCssClass).addEventListener('click', function (event) {
                event.preventDefault();
                toggleTableMaskingStatus(instance);
                showOrHideColumns(instance);
            });
        }


        // * DRAG & DROP REORDERING *

        const $tableBody = instance._table.querySelector('tbody');
        if ($tableBody.classList.contains(instance._settings.sortableCssClass)) {
            new Sortable($tableBody, {
                handle: '.' + instance._settings.sortHandleCssClass,
                animation: 150,
                filter: '.' + instance._settings.sumLineCssClass,
                onUpdate: function (event) {
                    if (event.newIndex === 0)
                        instance._settings.moveBeforeFunction(event.item.id, event.item.nextElementSibling.id);
                    else
                        instance._settings.moveAfterFunction(event.item.id, event.item.previousElementSibling.id);
                    zebra(instance);
                }
            });
        }
    }


    // * FILTERING *

    function filter(instance) {
        let didFilter = false;
        instance._table.querySelectorAll('.' + instance._settings.formElementFilterCssClass).forEach(function (filterField) {
            const filterName = filterField.name;
            const filterVal = filterField.value.trim().toLowerCase();
            if (filterVal !== '') {
                instance._table.querySelectorAll('td.' + filterName).forEach(function (cell) {
                    if (!cell.parentElement.classList.contains(instance._settings.sumLineCssClass)) {
                        let content;
                        if (cell.dataset.filterValue)
                            content = cell.dataset.filterValue.toLowerCase();
                        else
                            content = cell.textContent.toLowerCase();
                        if (content.indexOf(filterVal) > -1) {
                            if (!didFilter)
                                cell.parentElement.classList.remove(instance._settings.filteredCssClass);
                        } else {
                            cell.parentElement.classList.add(instance._settings.filteredCssClass);
                        }
                    }
                });
                didFilter = true;
            }
            setCookie(instance._table, filterName, filterField.value.trim());
        });
        if (!didFilter)
            removeFiltering(instance);
        updateFilteringCounters(instance);
        updateSums(instance);
        zebra(instance);
    }

    function setCookie($table, col, value) {
        const name = "cctable|" + $table.id + '|' + col;
        document.cookie = name + "=" + encodeURIComponent(value) + '; secure';
    }

    function removeFiltering(instance) {
        let count = 0;
        instance._table.querySelectorAll('tr').forEach(function (line) {
            line.classList.remove(instance._settings.filteredCssClass);
            ++count;
        });
        updateFilteringCounters(instance);
        updateSums(instance);
        zebra(instance);
    }

    function updateFilteringCounters(instance) {
        const idTable = instance._table.id;
        const total = document.querySelector('#' + idTable + "_total").textContent;
        const filteredOut = instance._table.querySelectorAll('tr.' + instance._settings.filteredCssClass).length;
        const shown = total - filteredOut;

        document.querySelector('#' + idTable + "_shown").textContent = shown.toString();
        document.querySelector('#' + idTable + "_filtered_out").textContent = filteredOut.toString();
    }

    function advancedSearch(instance) {
        const $form = document.querySelector('form[name="' + instance._settings.advancedSearchFormName + '"]');
        const filterName = $form.querySelector('input[name="tb-filter-name"]').value;
        const keywords = $form.querySelector('textarea[name="tb-search-keywords"]').value.trim();
        const andModality = $form.querySelector('input[name="tb-filter-mods"]:checked').value === 'AND';

        const keywordList = keywords === '' ? [] : keywords.split(/\s+/);

        // * All basic search elements removed before advanced search (combination would make no sense)
        instance._table.querySelectorAll('.' + instance._settings.formElementFilterCssClass).forEach(function (filterField) {
            filterField.value = '';
        });
        removeFiltering(instance);

        if (keywordList.length > 0) {
            if (andModality) {
                instance._table.querySelectorAll('td.' + filterName).forEach(function (cell) {
                    if (!cell.parentElement.classList.contains(instance._settings.sumLineCssClass)) {
                        let content;
                        if (cell.dataset.filterValue)
                            content = cell.dataset.filterValue.toLowerCase();
                        else
                            content = cell.textContent.toLowerCase();
                        let foundAll = true;
                        for (let i = 0; i < keywordList.length; ++i) {
                            if (content.indexOf(keywordList[i]) === -1) {
                                foundAll = false;
                                break;
                            }
                        }
                        if (!foundAll)
                            cell.parentElement.classList.add(instance._settings.filteredCssClass);
                    }
                });
            } else {
                instance._table.querySelectorAll('td.' + filterName).forEach(function (cell) {
                    if (!cell.parentElement.classList.contains(instance._settings.sumLineCssClass)) {
                        let content;
                        if (cell.dataset.filterValue)
                            content = cell.dataset.filterValue.toLowerCase();
                        else
                            content = cell.textContent.toLowerCase();
                        let missing = true;
                        for (let i = 0; i < keywordList.length; ++i) {
                            if (content.indexOf(keywordList[i]) > -1) {
                                missing = false;
                                break;
                            }
                        }
                        if (missing)
                            cell.parentElement.classList.add(instance._settings.filteredCssClass);
                    }
                });
            }
        }

        updateFilteringCounters(instance);
        updateSums(instance);
        zebra(instance);
        instance._settings.hideAdvancedSearchModale();
    }

    function clearFilters(instance) {
        instance._table.querySelectorAll('.' + instance._settings.formElementFilterCssClass).forEach(function (filterField) {
            filterField.value = '';
            setCookie(instance._table, filterField.name, '');
        });

        removeFiltering(instance);
    }

    function readCookies(instance) {
        if (document.cookie === "")
            return;

        const startOfName = "cctable|" + instance._table.id + "|";
        const cookies = document.cookie.split(";");
        for (let i = 0; i < cookies.length; i++) {
            const nameValuePair = cookies[i].split("=");
            const name = nameValuePair[0].trim();
            if (name.indexOf(startOfName) === 0) {
                const value = decodeURIComponent(nameValuePair[1]);
                const info = name.split("|");
                const field = info[2];

                const filterField = instance._table.querySelector('.' + instance._settings.formElementFilterCssClass + '[name="' + field + '"]');
                if (filterField)
                    filterField.value = value;
            }
        }
        filter(instance);
    }


    // * SORTING *

    function sort(instance, sortColumn) {
        if (!instance._sortingDirection[sortColumn])
            instance._sortingDirection[sortColumn] = 'asc';

        const sortVals = [];
        const tds = { };

        let sumLine = undefined;

        let index = 0;
        instance._table.querySelectorAll('td.' + sortColumn).forEach(function (cell) {
            if (cell.parentElement.classList.contains(instance._settings.sumLineCssClass))
                sumLine = cell.parentElement;
            else {
                let val;
                if (cell.dataset.sortValue)
                    val = cell.dataset.sortValue;
                else
                    val = cell.textContent;
                val += '~' + index;
                sortVals.push(val);
                tds[val] = cell.parentElement;

                ++index;
            }
        })

        sortVals.sort();
        if (instance._sortingDirection[sortColumn] === 'desc')
            sortVals.reverse();

        const $content = instance._table.querySelector('tbody');
        $content.innerHTML = '';
        const length = sortVals.length;
        for (let i = 0; i < length; ++i) {
            $content.append(tds[sortVals[i]]);
        }
        if (sumLine)
            $content.append(sumLine);

        if (instance._sortingDirection[sortColumn] === 'asc')
            instance._sortingDirection[sortColumn] = 'desc';
        else
            instance._sortingDirection[sortColumn] = 'asc';

        zebra(instance);
    }


    // * COLUMNS MASKING *

    function tableDoesMasking(instance) {
        return tableShowingAllData(instance) || tableMaskingSomeData(instance);
    }

    function tableShowingAllData(instance) {
        return instance._table.classList.contains(instance._settings.showMoreCssClass);
    }

    function tableMaskingSomeData(instance) {
        return instance._table.classList.contains(instance._settings.showLessCssClass);
    }

    function showOrHideColumns(instance) {
        if (tableShowingAllData(instance)) {
            instance._table.querySelectorAll('.' + instance._settings.maskableCssClass).forEach(function (maskableCell) {
                maskableCell.classList.remove(instance._settings.maskedCssClass);
            });
        }

        if (tableMaskingSomeData(instance)) {
            instance._table.querySelectorAll('.' + instance._settings.maskableCssClass).forEach(function (maskableCell) {
                maskableCell.classList.add(instance._settings.maskedCssClass);
            });
        }
    }

    function toggleTableMaskingStatus(instance) {
        const $showMoreLink = document.querySelector('#' + instance._table.id + '-masking-link-show');
        const $showLessLink = document.querySelector('#' + instance._table.id + '-masking-link-hide');

        if (tableShowingAllData(instance)) {
            instance._table.classList.remove(instance._settings.showMoreCssClass);
            instance._table.classList.add(instance._settings.showLessCssClass);
            $showMoreLink.classList.remove(instance._settings.maskedCssClass);
            $showLessLink.classList.add(instance._settings.maskedCssClass);
            return;
        }

        if (tableMaskingSomeData(instance)) {
            instance._table.classList.remove(instance._settings.showLessCssClass);
            instance._table.classList.add(instance._settings.showMoreCssClass);
            $showMoreLink.classList.add(instance._settings.maskedCssClass);
            $showLessLink.classList.remove(instance._settings.maskedCssClass);
            return;
        }

        throw "Masking operation called on table that doesn't support masking";
    }


    // * SUM *

    function updateSums(instance) {
        if (hasSummationRow(instance)) {
            for (const sumFieldClass of getSumFieldClasses(instance))
                updateSum(instance, sumFieldClass);
        }
    }

    function hasSummationRow(instance) {
        return !!instance._table.querySelector('tr.' + instance._settings.sumLineCssClass);
    }

    function getSumFieldClasses(instance) {
        const sumFieldClasses = [ ];
        for (const cell of instance._table.querySelector('tr.' + instance._settings.sumLineCssClass).children) {
            const cssClasses = cell.classList;
            if (cssClasses.contains(instance._settings.sumCellCssClass)) {
                for (const cssClass of cssClasses) {
                    if (cssClass.startsWith('tb-') && cssClass !== instance._settings.sumCellCssClass)
                        sumFieldClasses.push(cssClass);
                }
            }
        }
        return sumFieldClasses;
    }

    function updateSum(instance, sumFieldClass) {
        let sum = 0;

        instance._table.querySelectorAll('tbody tr td.' + sumFieldClass).forEach(function (cell) {
            if (!cell.classList.contains(instance._settings.sumCellCssClass) && !cell.parentElement.classList.contains(instance._settings.filteredCssClass)) {
                let value;
                if (cell.dataset.sumValue)
                    value = cell.dataset.sumValue;
                else
                    value = cell.textContent;
                const numericValue = Number(value);
                if (!isNaN(numericValue))
                    sum += numericValue;
            }
        });

        instance._table.querySelector('tr.' + instance._settings.sumLineCssClass + ' td.' + sumFieldClass).textContent =
            instance._settings.sumFormat(sum, instance._settings.sumShowZeroes);
    }


    // * ZEBRA *

    function zebra(instance) {
        if (instance._settings.zebraActive) {
            let index = 0;
            instance._table.querySelectorAll('tbody tr').forEach(function (line) {
                if (!line.classList.contains(instance._settings.filteredCssClass)) {
                    ++index;
                    if (index % 2 === 0) {
                        line.classList.add(instance._settings.zebraCssClass);
                        //console.log("zebra add: " + line.id);
                    } else {
                        line.classList.remove(instance._settings.zebraCssClass);
                        //console.log("zebra remove: " + line.id);
                    }
                } else {
                    //console.log("zebra ignored: " + line.id);
                }
            });
        }
    }


    // * CONSTRUCTOR *

    let Constructor = function (tableRef, options = { }) {
        let settings = Object.assign({
            // * FILTERING *
            formElementFilterCssClass: 'tb-filter',
            removeFilteringLinkCssClass: 'tb-nofilter',
            filteredCssClass: 'tb-filtered',
            advancedSearchFormName: 'tb-advanced-search-form',
            showAdvancedSearchModale: function (idTable, filterName) {
                console.log("SHOW ADVANCED SEARCH MODALE: tb ID = " + idTable + ", name = " + filterName);
            },
            hideAdvancedSearchModale: function () {
                console.log("HIDE ADVANCED SEARCH MODALE");
            },

            // * SORTING *
            thSortableTitleCssClass: 'tb-sort',

            // * COLUMNS MASKING *
            maskedCssClass: 'tb-masked',
            showMoreCssClass: 'tb-show-more',
            showLessCssClass: 'tb-show-less',
            maskableCssClass: 'tb-maskable',
            maskingLinkCssClass: 'tb-masking-link',

            // * DRAG & DROP REORDERING *
            sortableCssClass: "tb-sortable",
            sortHandleCssClass: "tb-reorder",
            moveAfterFunction: function (itemId, moveAfterItemId) {
                console.log('Moved element ID = ' + itemId);
                console.log('To be moved after element with ID = ' + moveAfterItemId);
            },
            moveBeforeFunction: function (itemId, moveBeforeItemId) {
                console.log('Moved element ID = ' + itemId);
                console.log('To be moved before element with ID = ' + moveBeforeItemId);
            },

            // * SUM *
            sumLineCssClass: "tb-summation-line",
            sumCellCssClass: "tb-summation-data",
            sumShowZeroes: false,
            sumFormat: function (sum, showZeroes) {
                if (sum === 0 && !showZeroes)
                    return '';
                return sum;
            },

            // * ZEBRA *
            zebraActive: true,
            zebraCssClass: 'alternate'
        }, options);
        Object.freeze(settings);

        let $table = document.querySelector(tableRef);

        Object.defineProperties(this, {
            _table: { value: $table },
            _settings: { value: settings },
            _sortingDirection: { value: { }, writable: true }
        });

        createEventListeners(this);
    }

    return Constructor;

}) ();
