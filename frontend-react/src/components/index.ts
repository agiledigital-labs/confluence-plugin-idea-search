import { JSONSchema7 } from "json-schema";

declare global {
  interface Window {
    tinymce: {
      confluence: {
        MacroUtils: { insertMacro: (input: object) => void };
      };
    };
    AJS?: {
      contextPath: () => string;
      dialog2: any;
      $: any;
      Rte: {
        getEditor: () => any;
        BookmarkManager: { storeBookmark: () => void };
      };
      Confluence: {
        Editor: { getContentId: () => string };
        PropertyPanel: {
          Macro: {
            registerButtonHandler: (
              var1: string,
              var2: (e: any, macroNode: any) => void
            ) => void;
          };
        };
      };
    };
  }
}

const indexType = ["string", "number", "boolean", "static"] as const;

export type IndexType = typeof indexType[number];

// the rest endpoint version
export const version: string = "1";

export type IndexItem = {
  key: string;
  type: IndexType;
  index?: number;
  value: string | number | boolean;
};

export interface FormDataType {
  schema?: JSONSchema7;
  uiSchema?: JSONSchema7;
  indexSchema?: { index?: Array<Omit<IndexItem, "value">> };
}
