import React, { useCallback, useRef, useState } from "react";
import { debounce } from "lodash";
import axios from "axios";
import {
  Avatar,
  FormControl,
  Input,
  InputLabel,
  List,
  ListItem,
  ListItemIcon,
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
const AJS = window.AJS ? window.AJS : undefined;

export const UserSelection = (props: any) => {
  const classes = useStyles();
  const [user, setUser] = useState<string>(props.value);
  const [userList, setUserList] = useState<
    Array<{ username: string; userKey: string; href: string }>
  >([]);
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "";
  const userSearch = useCallback(
    debounce((userInput: string) => {
      axios.get(`${contextPath}/rest/api/search?cql=${userInput}`).then((res) =>
        setUserList(
          res.data.result.map(
            ({
              username,
              userKey,
              thumbnailLink: { href },
            }: {
              username: string;
              userKey: string;
              thumbnailLink: {
                href: string;
                type: string;
                rel: string;
              };
            }) => ({ username, userKey, href })
          )
        )
      );
    }, 1000),
    []
  );

  const updateUser = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUser(event.target.value);
    props.onChange(event.target.value);
    userSearch(event.target.value);
  };

  const inputEl = useRef(null);
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
          onChange={updateUser}
          className="autocomplete-multiuser MuiInputBase-input MuiInput-input"
          value={user}
          required={props.required}
        />
      </FormControl>
      <Popover
        id="user-popover"
        open={userList.length > 0}
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
          <Typography>Select a user</Typography>
          <List component="nav" aria-labelledby="nested-list-subheader">
            {userList
              ? userList.map(({ username, userKey, href }) => (
                  <ListItem
                    button
                    onClick={() => {
                      setUser(username);
                      props.onChange(`${username}::${userKey}`);
                      setUserList([]);
                    }}
                  >
                    <ListItemIcon>
                      <Avatar alt="username" src={href} />
                    </ListItemIcon>
                    <ListItemText primary={username} />
                  </ListItem>
                ))
              : undefined}
          </List>
        </div>
      </Popover>
    </>
  );
};

export default UserSelection;
