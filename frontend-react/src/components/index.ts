declare global {
  interface Window {
    AJS?: {
      Confluence: {
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

export { default as Form } from "./Table";
export { default as Idea } from "./Idea";
