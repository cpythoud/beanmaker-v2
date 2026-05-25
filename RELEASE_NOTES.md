# Release Notes for Beanmaker

## v2.0 — 2024-07-02

- Move from `1.0-SNAPSHOT` to an actual version number.
- The libraries are now deployed in enough projects that they are no longer considered alpha or beta.
- Versioning is now required for projects that move at different speeds.

## v2.0.1 — 2024-07-23

- Add `removeFileExtension()` to the `Files` class.
- Update version management.

## v2.1 — 2024-08-01

- Introduce the possibility to initialize the database reference in DbBeans through an ad hoc class.
- In package `util`, add methods to read resource files as a string or as a list of strings.
- In package `util`, add the `Process` utility class to manage execution of external processes.
- In package `util`, add new methods to create `Date`, `Time`, and `Timestamp` objects.
- Remove custom logging framework.

## v2.2 — 2024-12-24

### Incompatible Changes

- Simplify data formatting by removing:
    - `BeanFormatter`
    - `BeanFormatterBase`
    - `BeanFormatterInterface`
    - `BeanFormatterInterfaceBase`
- Reengineer:
    - `LocalDbBeanFormatter`
    - `FormattedBeanData`
    - `FormattedBeanDataBase`

  These classes now pick up the slack from the removed formatter classes.

- Reengineer `BeanParameter` and `BeanParameterBase` to fully enforce the singleton approach:
    - `BeanParameter` becomes an enum.
    - `BeanParameterBase` is now an interface.

> **Important:** Because of the changes above, the code for all beans must be regenerated, and files that are no longer created by this version must be deleted from your codebase.

### Bug Fixes and Minor Changes

- Fix a bug in `SingleElements` from `runtime.dbutils` when retrieving elements in a `DBTransaction`.
- Fix a bug in code generation for labels that are not required: no per-language value should be reported as required if the field itself is not required.
- Fix a bug where an empty label was created for an optional label field instead of leaving the field empty.
    - This fix includes the addition of `cachedValuesExist()` to the `DbBeanEditor` interface, which will need to be implemented by client libraries/programs.
- Fix bug: CSV importer classes were not correctly referencing column names for boolean fields.
- In code generator, when reporting a field that is too long, add the length of the field as a second argument to the error message composing function.
- Add possibility to dynamically instantiate bean editors to the runtime.
- Add `BeanEditorInterface` to the runtime to allow client classes that mimic an editor to implement it without subclassing `DbBeanEditor`.

## v2.3 — 2025-05-25

### Incompatible Changes

- Remove leaked `FileItem` from `HttpRequestParameters` and replace it with the inner class `UploadedFile`.

- Revamp field validation with:
    - Introduction of `FieldValidationFunction` to replace complex generic arguments used in lists of validation functions.
    - Introduction of a warning level in bean validation logic.
    - Renamed validation checking functions to distinguish:
        - warnings,
        - errors that should short-circuit the validation logic,
        - errors that should be reported while letting the validation process continue.

- Introduce the notion of region to `DbBeanLanguage` for finer localization, for example `en-US` vs `en`.

- Add support for a hierarchy of languages in multilingual projects:
    - When a label value for a language + region is not available, the value for the raw language is substituted.
    - If this value is also unavailable, the value for the default language is then substituted.
    - This mimics how Java resource files work.

- Add support to use `id_label`, `id_name_label`, and `id_description_label` fields, if present, to determine the “name” of a bean.
    - This name is used in tiles and lists.

- Add support in `BeanParametersBase` to record database tables where the bean is referenced and to check the presence of such references.
    - This is used in the implementation of `okToDelete()` in master tables.
    - Because of this, all `BeanMasterTableViewBase` source files need to be regenerated.

- Change default error message container IDs to the following form:

  ```text
  error_message_<Bean name>_<Bean ID>
  ```

  This guarantees uniqueness when creating complex interfaces. Existing code might need to be changed if it relies on default generation of error message containers.

- Refactor `MoneyFormat` to better correspond to the standard Builder Pattern.
    - This could theoretically affect existing code.
    - `Money`, `MoneyFormat`, and related classes will be deprecated in version 2.4.
    - Consider replacing them with `DecimalValue` and related classes.

- Refactor `SingleElements` editor retrieval functions to accept arguments of type:

  ```java
  E extends DbBeanEditorInterface
  ```

  instead of:

  ```java
  E extends DbBeanEditor
  ```

  This change does not strictly introduce an incompatibility, but calling code should probably be recompiled.

- Completely revamp code management, with liberal use of:
    - `DbBeanWithUniqueCode`
    - `DbBeanEditorWithUniqueCode`

  Affected code will need to be regenerated and recompiled to take advantage of the new functionality.

- Make two functions in `BaseMasterTableView` private instead of protected:
    - `getTableCell()`
    - `getTableCellCssClasses()`

  `getTableCell()` functions should no longer be called, except for the one with a `MasterTableCellDefinition` argument.

  A few `getBasicTableCell()` functions have been added to keep displaying straightforward custom data simple.

> **Important:** All generated code from version 2.2 must be regenerated.
>
> In editable files, overloaded functions related to file handling, data validation, bean naming, and bean ordering should be reviewed.

### New Features

- Add two separate annotations to distinguish generated files that can be edited from those that should not be edited.
    - These replace the use of `javax.annotation.processing.Generated`.

- Add support for `ApplicationParameters` singleton.
    - This centralizes application parameters.
    - It allows data libraries built with Beanmaker to be used in more than one project/deliverable.

- Add support for subcontainers in forms.
    - Form fields can be placed in one subcontainer.
    - Form buttons can be placed in another.
    - This helps take advantage of CSS frameworks that separate body and footer sections in modals.

- Introduce Bootstrap 5 `HtmlFormHelper`.

- Introduce direct support in `BeanMasterTableViewBase` for links to a detail view.
    - This removes the need for repetitive overloading of the `getEditCell()` function.

- Add `sortOnHeaderClick` boolean to master tables.
    - Toggles sorting of table lines when the user clicks a column header.
    - Enabled by default.
    - Allows disabling sorting on tables with drag-and-drop `item_order` rearrangement.

- Create `LocalLabelCache` to cache labels in multilingual master tables.
    - For large tables, this considerably reduces the number of database queries.
    - This is especially useful when safe labels are used.
    - The number of queries can go from thousands to barely a dozen.

- Add new parameter `extraFormRequestParameters` to `beanmaker2.js`.
    - This is a JavaScript array of parameters to look up in the dataset of the edit link.
    - These parameters are translated into request parameters with their corresponding values when the web form is posted.
    - This helps when extra information is required to initialize a new record/bean.

- Add function to check if a label is unique in context for a specific table field.

- Add `DbBeanHtmlSelector` to the runtime library as a helper class to generate HTML selects from a list of beans.

- Enhance robustness of CSV parsing classes and functions.
    - Avoid unnecessary exceptions when fields are empty.

- Add function to generate codes from label content.
    - Example: `"My Data"` becomes `my_data`.

- Make `DbBeanLocalization` in views no longer final.
    - This is most important for master tables.
    - It can now be reassigned in superclasses.

- Add function to help create select-based filters in tables when:
    - a small inventory of values/beans is present,
    - strict type filtering is preferable to string-based filtering.

- Introduce new `DecimalValue` class as an available data type for code generation.
    - This allows easy management of numeric values with decimals.
    - Values are stored as integers in the database to avoid rounding errors.
    - This class should be used instead of `Money` from now on.
    - `Money` will be deprecated in version 2.4 and later removed.

- Introduce a new servlet operation to display a MasterTable.
    - This allows a table to be downloaded from an Ajax request.
    - It also allows, among other things, displaying a loading indicator while downloading large tables.

- Introduce a new servlet operation to separate generation of:
    - the HTML form,
    - the buttons that control it.

  This should be useful when these elements need to be split, as in modals.

  Support has been added in:
    - the runtime,
    - `beanmaker2.js`,
    - the Bootstrap 5 implementation of `HtmlFormHelper`.

- Introduce two functions in `BaseMasterTableView`:
    - `showEditLink()`
    - `showDetailLink()`

  These determine, on a line-by-line basis, when to display edit and detail links.

  By default, the functions return the values of:
    - `showEditLinks`
    - `showDetailLinks`

  respectively. They are intended to be overloaded in superclasses.

- Introduce versioned beans.
    - This is a mechanism to preserve modification history for some data structures.

- Introduce transacted versions of:
    - `getSelection()`
    - `getSelectionCount()`
    - `getCount()`

  in `BeanBase`.

- In `BaseMasterTableView`, augment the granularity of helper functions used to compose action links in master tables.
    - This facilitates precise overloading of behavior in client applications.
    - It minimizes code copied from `BaseMasterTableView` into client code.
    - Some member variables were also given protected access for the same purpose.

- Augment language support to allow more control over field labels for multi-language form fields.
    - This allows more descriptive labels for non-technical users of applications created with Beanmaker.

### Bug Fixes

- Fix bug: drag and drop does not work for tables with an `item_order` linked to another field.
- Fix bug: cannot create more than one instance of `Beanmaker2` JavaScript object on the same page.
- Fix bug: null `String` saved as empty text instead of a SQL `NULL` value.
- Fix bug: `MoneyFormat` builder does not work correctly because the default format leaks into all builder format creation.
    - It also does not work at all if the builder is used to format strings.
- Fix bug: not losing the ID of a label during data validation.
    - This allows uniqueness tests on label content.
    - It makes sure orphaned labels are not created on every update.
- Fix bugs appearing in some `item_order` edge cases.

### Minor Changes

- Clean up code in master tables.
- Improve some exception messages.
- Add various small improvements and fixes.
- Add various utility functions.
- Replace manual composition of JSON strings with `org.json` library calls.