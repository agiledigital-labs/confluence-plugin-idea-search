import React, { useState } from "react";
import Modal, { ModalTransition } from "@atlaskit/modal-dialog";
import OuterForm from "./Form";

const OuterModal = ({ onClose, initFormData }: any) => {
  const [formData, setFormData] = useState<object>(initFormData);
  return (
    <ModalTransition>
      <Modal
        scrollBehavior={"inside"}
        onClose={onClose(JSON.stringify(formData))}
        width={"x-large"}
        heading="Edit the data in this form"
      >
        <OuterForm {...{ formData, setFormData }} />
      </Modal>
    </ModalTransition>
  );
};

export default OuterModal;
