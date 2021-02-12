import React, { useCallback, useRef, useState } from "react";
import { debounce } from "lodash";
import axios from "axios";
import {
  FormControl,
  Input,
  InputLabel,
  List,
  ListItem,
  ListItemText,
  Popover,
  Typography,
} from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles(() => ({
  root: {
    width: "100%",
  },
  popover: {
    width: "100%",
    padding: "1rem",
    margin: "1px",
  },
}));

export const RestSelection = (props: any) => {
  console.log(props);
  const classes = useStyles();
  const [rest, setRest] = useState<string>(props.value);
  const [restList, setRestList] = useState<Array<string>>([]);
  const restSearch = useCallback(
    debounce((restInput: string) => {
      axios
        .get(`${props.uiSchema.context}/${props.uiSchema.endpoint}${restInput}`)
        .then((res) => setRestList(res.data.result));
    }, 1000),
    []
  );

  const updateRest = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRest(event.target.value);
    props.onChange(event.target.value);
    restSearch(event.target.value);
  };

  const inputEl = useRef(null);
  const test = "test";
  test.search("::");
  return (
    <>
      <FormControl fullWidth={true} required={props.required} ref={inputEl}>
        <InputLabel>{props.label}</InputLabel>
        <Input
          disabled={props.disabled || props.readonly}
          id={`${props.id}-key`}
          name={`${props.id}-key`}
          onBlur={!props.readonly ? props.handleBlur : undefined}
          type="text"
          onChange={updateRest}
          className="autocomplete-multi MuiInputBase-input MuiInput-input"
          value={rest}
          required={props.required}
        />
      </FormControl>
      <Popover
        id="simper-test"
        open={restList.length > 0}
        anchorEl={inputEl.current}
        className={classes.root}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
      >
        <div className={classes.popover}>
          <Typography>Select a rest</Typography>
          <List component="nav" aria-labelledby="nested-list-subheader">
            {restList
              ? restList.map((item, index) => (
                  <ListItem
                    button
                    key={index}
                    onClick={() => {
                      setRest(item);
                      props.onChange(item);
                      setRestList([]);
                    }}
                  >
                    <ListItemText primary={item} />
                  </ListItem>
                ))
              : undefined}
          </List>
        </div>
      </Popover>
    </>
  );
};

export default RestSelection;
