import {
  get,
  isArray,
  isBoolean,
  isNumber,
  isObject,
  isString,
  unescape,
} from "lodash/fp";

type FormDataItem = string | number | boolean;

type FormDataPossible =
  | FormDataItem
  | [FormDataItem]
  | FormDataStorageType
  | [FormDataStorageType];

type FormDataStorageType = {
  [key: string]: FormDataPossible | { [key: string]: FormDataPossible };
};

const renderPrimitive = (input: FormDataItem): string => `<p>${input}</p>`;

const renderListItems = (
  arrayInput: Array<FormDataItem | FormDataStorageType>,
  level: number
): string =>
  arrayInput
    .map((input: FormDataPossible) => {
      if (isArray(input) || isObject(input)) {
        return recursive(input, level);
      } else {
        return `<li>${input}</li>`;
      }
    })
    .join("");

const renderArrayPrimtive = (
  arrayInput: Array<FormDataItem | FormDataStorageType>,
  level: number
): string => `<ul>
${renderListItems(arrayInput, level)}
</ul>`;

export const renderObject = (
  inputObj: FormDataPossible,
  level: number = 1
): string =>
  Object.keys(inputObj)
    .map(
      (title) =>
        `<h${level + 1 <= 7 ? level + 1 : 7}>${title}</h${
          level + 1 <= 7 ? level + 1 : 7
        }>${recursive(
          get(title, inputObj) as
            | FormDataPossible
            | { [key: string]: FormDataPossible },
          level
        )}`
    )
    .join("");

const recursive = (formData: FormDataPossible, level: number): string => {
  switch (true) {
    case isArray(formData):
      return renderArrayPrimtive(
        formData as Array<FormDataItem | FormDataStorageType>,
        level
      );
    case isObject(formData):
      return renderObject(formData, level + 1);
    case isString(formData):
      return renderPrimitive(formData as FormDataItem);
    case isNumber(formData):
      return renderPrimitive(formData as FormDataItem);
    case isBoolean(formData):
      return renderPrimitive(formData as FormDataItem);
    default:
      return "";
  }
};

const render = () => {
  if (
    window.document.getElementById("test") === null ||
    // @ts-ignore
    window.document.getElementById("testMount").value === null
  ) {
    setTimeout(render, 500);
  } else {
    console.log(
      // @ts-ignore
      unescape(window.document.getElementById("test").value)
        .trim()
        .replace(/\\"/g, '"')
        .replace('"}/', '"}')
        .replace(/}"/g, "}")
        .replace(/"{/g, "{")
        .replace(':"{"', ':{"')
    );
    // @ts-ignore
    const data = JSON.parse(
      // @ts-ignore
      unescape(window.document.getElementById("test").value)
        .trim()
        .replace(/\\"/g, '"')
        .replace('"}/', '"}')
        .replace(/}"/g, "}")
        .replace(/"{/g, "{")
        .replace(':"{"', ':{"')
    );
    console.log(renderObject(data));
    // @ts-ignore
    AJS?.$("#testMount").append(renderObject(data));
  }
};

render();
