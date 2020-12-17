const $ = AJS.$;

AJS.toInit(() => {
  // AJS.$("#technologies-input").auiSelect2();

  $("#technologies").auiSelect2({
    multiple: true,
    ajax: {
      url: `${AJS.contextPath()}/rest/idea/1/technology`,
      data: (params) => ({q: params}),
      processResults: (data) => ({
        results: data.map((item, index) => ({
          id: item.label.toLowerCase(),
          text: item.label,
        })),
      }),
      results: (data) => ({
        results: data.map((item, index) => ({
          id: item.label.toLowerCase(),
          text: item.label,
        })),
      }),
    },
  });

  $("#edit-technologies").on({
    "click": (event) => {
      AJS.dialog2("#technologies-dialog").show();

      $(".technology").each((index, element) => {
        const elementText = $(element).text();
        console.log(elementText);
        $("#technologies").append(
            new Option(elementText, elementText, true, true)).trigger("change");
      });
    }
  });

  $("#technologies-dialog").on("keyup", ".select2-input",
      ({target, keyCode}) => {
        console.log("Key up");
        if (keyCode === 13) {
          console.log("Press Enter");
          $(target).val(`${$(target).val()},`);
        }
      });
});