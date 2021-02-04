declare global {
  interface Window {
    AJS?: {
      dialog2: any;
      $: any;
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

export { default as Idea } from "./Idea";
