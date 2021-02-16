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

// the rest endpoint version
export const version: string = "1";
