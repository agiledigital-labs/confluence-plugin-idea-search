import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import DynamicTable from "@atlaskit/dynamic-table";

interface IdeaPage {
  owner?: String;
  status?: String;
  technologies?: String;
  title?: String;
  description?: String;
  url: string;
}

const OuterTable = () => {
  const [justPages, setJustPages] = useState<Array<IdeaPage>>();

  useEffect(() => {
    axios
      .get("http://shouv-box:1990/confluence/rest/idea/1/ideaPages")
      .then((response) => setJustPages(response.data));
  }, []);

  const rows = justPages?.map((page: IdeaPage) => ({
    key: `row-${page.title}`,
    cells: [
      {
        key: `cell-${page.title}`,
        content: <a href={page.url}>{page.title}</a>,
      },
      {
        key: `cell-${page.description}`,
        content: page.description?.replace(/<[^>]+>/g, ""),
      },
      {
        key: `cell-${page.technologies}`,
        content: page.technologies?.replace(/<[^>]+>/g, ""),
      },
      {
        key: `cell-${page.status}`,
        content: page.status?.replace(/<[^>]+>/g, ""),
      },
      {
        key: `cell-${page.owner}`,
        content: (
          <a href={"http://shouv-box:1990/confluence/display/~" + page.owner}>
            {"@" + page.owner}
          </a>
        ),
      },
    ],
  }));

  const headers = ["Name", "Description", "Technologies", "Status", "Talk to"];

  const head = {
    cells: headers.map((header) => ({
      key: header,
      content: header,
    })),
  };

  return (
    <DynamicTable
      head={head}
      rows={rows}
      rowsPerPage={10}
      defaultPage={1}
      loadingSpinnerSize="large"
      isLoading={false}
      isFixedSize
      defaultSortKey="term"
      defaultSortOrder="ASC"
      onSort={() => console.log("onSort")}
      onSetPage={() => console.log("onSetPage")}
    />
  );
};

export default OuterTable;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("container");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterTable />, wrapper) : false;
});
