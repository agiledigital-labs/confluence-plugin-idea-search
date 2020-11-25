const $ = AJS.$

let rowStatus;
let technlogies = new Set();
let usedTechnlogies = [];

let multiSelectFocus = {
  technlogies: {
    container: false,
    list: false
  }
}

const setHidden = () => {
  $('.table-content tr').each((index, element) => {
    $(element)[Object.values(rowStatus[index]).some((value) => !value)
        ? 'addClass'
        : 'removeClass']('hidden');
  })
}

const addTechnologyOptionsToList = () => {
  technlogies.forEach((value) => {
    $('#technologies-list').append(
        `<li class="list-option" role="option">${value}</li>`);
  });
}

const removeTechnologyOptionsToList = () => {
  $('#technologies-list').empty();
}

const changeStatusOfTechnologiesList = () => {
  $('#technologies-list')[Object.values(multiSelectFocus.technlogies).some(
      (value) => value) ? 'removeClass' : 'addClass']('hidden');
}

const addTechnologyToMultiSelect = (element) => {
  $('#search-technologies').before(
      `<span class='tag'>${$(
          element).text()}<button type="button" class="close">&#x2715</button></span>`);
  $(element).addClass('hidden');
  usedTechnlogies.push(element);
  calculateHiddenRowForTechnologies();
}

const removeTechnologyFromMultiSelect = (element) => {
  const text = $(element).text().slice(0, -1);
  const listItem = usedTechnlogies.filter(
      (value) => $(value).text() === text)[0];
  $(listItem).removeClass('hidden');

  usedTechnlogies = usedTechnlogies.filter((value) => $(value).text() !== text);
  calculateHiddenRowForTechnologies();
}

const setHiddenFlagsOnTechnologyList = (searchValue) => {
  const technologiesLabels = usedTechnlogies.map((value) => $(value).text());

  $('#technologies-list li').each((index, value) => {
    if (!technologiesLabels.includes($(value).text())) {
      $(value)[$(value).text().toLowerCase().includes(searchValue)
          ? 'removeClass' : 'addClass']('hidden');
    }
  });
}

const calculateHiddenRowForTechnologies = () => {
  const techList = usedTechnlogies.map((value) => $(value).text());

  $('.cell-technology').each((index, value) => {
    let hasTechnology = [];

    if(techList.length > 0) {
      const rowList = []

      $(value).children('span.technology').each((index, technology) => {
        rowList.push($(technology).text());
      });

      techList.forEach((value) => hasTechnology.push(rowList.includes(value)));
    } else {
      hasTechnology.push(true);
    }

    rowStatus[index].technologies = hasTechnology.every((value) => value);
  });

  setHidden();
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

  (() => {
    let tempList = [];

    $('span.technology').each((index, value) => {
      tempList.push($(value).text());
    });

    tempList.sort();
    tempList.forEach((value) => technlogies.add(value));
  })();

  addTechnologyOptionsToList();

  $('#search-title').on('input', () => {
    const searchValue = $('#search-title').val().toLowerCase();

    $('.cell-title').each((index, value) => {
      rowStatus[index].name = searchValue === '' ? true : $(
          value).text().toLowerCase().includes(searchValue);
    });
    setHidden();
  });

  $('#search-technologies').on('input', () => {
    const searchValue = $('#search-technologies').val().toLowerCase();
    setHiddenFlagsOnTechnologyList(searchValue);
  });

  $('#search-technologies').on('keyup', (event) => {
    if (event.keyCode === 13 && $('#search-technologies').val() != '') {
      addTechnologyToMultiSelect($('#technologies-list li:not(.hidden):first'));
      $('#search-technologies').val('');
      setHiddenFlagsOnTechnologyList("");
    }
  });

  $('#technologies-multiselect-container').on('focusin', () => {
    multiSelectFocus.technlogies.container = true;
    changeStatusOfTechnologiesList();
  });

  $('#technologies-multiselect-container').on('focusout', () => {
    multiSelectFocus.technlogies.container = false;
    changeStatusOfTechnologiesList();
  });

  $('#technologies-list-container').on('mouseenter', () => {
    multiSelectFocus.technlogies.list = true;
    changeStatusOfTechnologiesList();
  });

  $('#technologies-list-container').on('mouseleave', () => {
    multiSelectFocus.technlogies.list = false;
    changeStatusOfTechnologiesList();
  });

  $('.list-option').on('click', ({target}) => {
    addTechnologyToMultiSelect(target);
  });

  $('#technologies-multiselect-container').on('click', 'button.close', ({target}) => {
    const parent = $(target).parent();
    removeTechnologyFromMultiSelect(parent);
    $(parent).remove();
  });
});