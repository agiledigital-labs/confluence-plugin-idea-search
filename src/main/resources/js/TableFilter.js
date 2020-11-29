const $ = AJS.$

const appConstants = {
  TECHNOLOGIES_LIST: 'technologies-list',
  TECHNOLOGIES_SEARCH: 'search-technologies',
  TECHNOLOGIES_COLUMN_CLASS: 'cell-technology',
  TECHNOLOGIES_COLUMN_KEY: 'technologies',
  STATUS_LIST: 'status-list',
  STATUS_SEARCH: 'search-status',
  STATUS_COLUMN_CLASS: 'cell-status',
  STATUS_COLUMN_KEY: 'status',
}

/**
 * Status of each column per row
 * @typedef {{name: boolean, description: boolean, technologies: boolean, status: boolean, talkTo: boolean}} RowColumnStatus
 * @type {RowColumnStatus[]}
 */
let rowStatus;

/**
 * Dropdown item lists
 * @type {Record<string, Set<string>>}
 */
let itemLists = {
  [appConstants.TECHNOLOGIES_LIST]: new Set(),
  [appConstants.STATUS_LIST]: new Set()
};

/**
 * List of technologies currently being filtered
 * @type {Record<string, HTMLElement[]>}
 */
let usedOptions = {
  [appConstants.TECHNOLOGIES_LIST]: [],
  [appConstants.STATUS_LIST]: []
};

/**
 * Stores the status of the focus of different components of the multiselect dropdown
 * @type {Record<string, {container: boolean, list: boolean}>}
 */
let multiSelectFocus = {
  [appConstants.TECHNOLOGIES_LIST]: {
    container: false,
    list: false
  },
  [appConstants.STATUS_LIST]: {
    container: false,
    list: false
  }
}

/**
 * Translate a column class to a column key
 * @type {{[p: string]: string}}
 */
const columnClassToColumnKey = {
  [appConstants.TECHNOLOGIES_COLUMN_CLASS]: appConstants.TECHNOLOGIES_COLUMN_KEY,
  [appConstants.STATUS_COLUMN_CLASS]: appConstants.STATUS_COLUMN_KEY
}

/**
 * Translate a list id to a column key
 * @type {{[p: string]: string}}
 */
const listToColumnClass = {
  [appConstants.TECHNOLOGIES_LIST]: appConstants.TECHNOLOGIES_COLUMN_CLASS,
  [appConstants.STATUS_LIST]: appConstants.STATUS_COLUMN_CLASS,
}

/**
 * Translate a input id to a list id
 * @type {{[p: string]: string}}
 */
const searchToList = {
  [appConstants.TECHNOLOGIES_SEARCH]: appConstants.TECHNOLOGIES_LIST,
  [appConstants.STATUS_SEARCH]: appConstants.STATUS_LIST
};

/**
 * Translate a list id to a input id
 * @type {{[p: string]: string}}
 */
const listToSearch = {
  [appConstants.TECHNOLOGIES_LIST]: appConstants.TECHNOLOGIES_SEARCH,
  [appConstants.STATUS_LIST]: appConstants.STATUS_SEARCH,
}

/**
 * Determines the visability of each row from the RowColumnStauts container
 */
const setHidden = () => {
  $('.table-content tr').each((index, element) => {
    $(element)[Object.values(rowStatus[index]).some((value) => !value)
        ? 'addClass'
        : 'removeClass']('hidden');
  })
}

/**
 * Generates the options for a dropdown list from a list of values
 *
 * @param {Set<string>} list A set of items to add to generate list items for
 */
const generateHTMLTagsForListItems = (list) => [...list].map(
    (value) => `<li class="list-option" role="option">${value}</li>`);

/**
 * Filters the dropdown list based off the statuses of each option
 *
 * @param {string} listName Name of the list to modify
 */
const changeStatusOfOptionInList = (listName) => {
  $(`#${listName}`)[Object.values(
      multiSelectFocus[listName]).some(
      (value) => value) ? 'removeClass' : 'addClass']('hidden');
}

/**
 * Adds a tag to the multiselect field. Hides item from the dropdown list
 *
 * @param {HTMLElement} element Item from dropdown to add
 * @param {string} searchField Id of search field
 * @param {string} list Id of dropdown list
 */
const addTagToMultiSelect = (searchField, list, element) => {
  if (element !== undefined) {
    $(`#${searchField}`).before(
        `<span class='tag'>${$(
            element).text()}<button type="button" class="close">&#x2715</button></span>`);
    $(element).addClass('hidden');
    usedOptions[list].push(element);
    calculateHiddenRowForColumn(list);
    return true;
  }

  return false;
}

/**
 * Removes a tag from the multiselect list. Item will show in the dropdown list
 * again
 *
 * @param {HTMLElement} element Item from multiselect field to remove
 * @param {string} list Id of dropdown list
 */
const removeTagFromMultiSelect = (list, element) => {
  const text = $(element).text().slice(0, -1);
  const listItem = usedOptions[list].filter(
      (value) => $(value).text() === text)[0];
  $(listItem).removeClass('hidden');

  usedOptions[list] = usedOptions[list].filter(
      (value) => $(value).text() !== text);
  calculateHiddenRowForColumn(list);
}

/**
 * Sets the visability status for a item in a dropdown list
 *
 * @param {string} listName Name of the unorder list in the dom
 * @param {string} searchValue Needle to search haystack for
 */
const setHiddenFlagsOnDropdownList = (listName, searchValue) => {
  const technologiesLabels = usedOptions[listName].map(
      (value) => $(value).text());

  $(`#${listName} li`).each((index, value) => {
    if (!technologiesLabels.includes($(value).text())) {
      $(value)[$(value).text().toLowerCase().includes(searchValue)
          ? 'removeClass' : 'addClass']('hidden');
    }
  });
}

/**
 * Checks each row of the table and sees if a column in that row fit the
 * crtiera of that in the filter box for that column
 *
 * @param {string} list Id of the dropdown list
 */
const calculateHiddenRowForColumn = (list) => {
  const optionsList = usedOptions[list].map(
      (value) => $(value).text());

  const columnClass = listToColumnClass[list];

  $(`.${columnClass}`).each((index, value) => {
    let hasOptions;

    if (optionsList.length > 0) {
      const rowList = [];

      $(value).children(`span.${columnClass.substring(5)}`).each(
          (index, option) => {
            rowList.push($(option).text());
          });

      hasOptions = optionsList.map((value) => rowList.includes(value));
    } else {
      hasOptions = [true];
    }

    rowStatus[index][columnClassToColumnKey[columnClass]] = hasOptions.every(
        (value) => value);
  });

  setHidden();
}

/**
 * Handles the focusing of a dropdown list
 *
 * @param {HTMLElement} element Unordered list or list item
 * @param isFocus Is the element currently in focus
 */
const handleListFocus = (element, isFocus) => {
  const listId = element.id.includes("list") ? element.id : $(element).parent(
      'ul').attr('id');
  multiSelectFocus[listId].list = isFocus;
  changeStatusOfOptionInList(listId);
}

/**
 * Handles the focusing of a search box container
 *
 * @param element Input field or the container of that field
 * @param isFocus Is the element currently focused
 */
const handleContainerFocus = (element, isFocus) => {
  const listId = searchToList[
      element.id.includes("search") ? element.id : $(element).closest(
          'div').children('input')[0].id];
  multiSelectFocus[listId].container = isFocus;
  changeStatusOfOptionInList(listId);
}

$(document).ready(() => {
  rowStatus = new Array($('.table-content tr').length).fill(undefined).map(
      () => ({
        name: true,
        description: true,
        technologies: true,
        status: true,
        talkTo: true
      }));

  ((lists) => {
    lists.forEach(({key, listRef}) => {
      let tempList = [];

      $(`span.${key}`).each((index, value) => {
        tempList.push($(value).text());
      });

      tempList.sort().forEach((value) => itemLists[listRef].add(value));

      $(`#${listRef}`).append(
          ...generateHTMLTagsForListItems(itemLists[listRef]));
    });
  })([{key: 'technology', listRef: appConstants.TECHNOLOGIES_LIST},
    {key: 'status', listRef: appConstants.STATUS_LIST}]);

  /*
   * Standard text search
   */
  $('.text-search').on('input', ({target}) => {
    const searchValue = $(target).val().toLowerCase();
    const columnClass = target.id.substring(7);

    $(`.cell-${columnClass}`).each((index, value) => {
      rowStatus[index][columnClass] = searchValue === '' ? true : $(
          value).text().toLowerCase().includes(searchValue);
    });
    setHidden();
  });

  /*
   * MultiSelect Type Ahead Event handlers
   */
  $(`.searchable`).on({
    'input': ({target}) => {
      setHiddenFlagsOnDropdownList(searchToList[target.id],
          $(target).val().toLowerCase());
    },
    'keyup': ({keyCode, target}) => {
      if (keyCode === 13 && $(target).val() != '') {
        const searchId = target.id;
        const listId = searchToList[searchId];

        if (addTagToMultiSelect(searchId, listId,
            $(`#${listId} li:not(.hidden):first`)[0])) {
          $(`#${searchId}`).val('');
          setHiddenFlagsOnDropdownList(listId, "");
        }
      }
    }
  });

  $('.multiselect-container').on({
    'focusin': ({target}) => handleContainerFocus(target, true),
    'focusout': ({target}) => handleContainerFocus(target, false)
  });

  $('.list-container').on({
    'mouseenter': ({target}) => handleListFocus(target, true),
    'mouseleave': ({target}) => handleListFocus(target, false)
  });

  $('.list-option').on('click', ({target}) => {
    const listId = $(target).parent().attr('id');
    addTagToMultiSelect(listToSearch[listId], listId, target);
  });

  $('.multiselect-container').on('click', 'button.close',
      ({target}) => {
        const parent = $(target).parent();
        removeTagFromMultiSelect(
            searchToList[parent.siblings('input').attr('id')], parent);
        $(parent).remove();
      });
});