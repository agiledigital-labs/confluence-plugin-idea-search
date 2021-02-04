import React, { useState } from "react";
import Modal, { ModalTransition } from "@atlaskit/modal-dialog";
import OuterIdea from "./Idea";

const OuterModal = ({ onClose, initFormData }: any) => {
  const [formData, setFormData] = useState<object>(initFormData);
  return (
    <ModalTransition>
      <Modal
        scrollBehavior={"inside"}
        onClose={onClose(JSON.stringify(formData))}
        width={"x-large"}
        heading="Edit your idea"
      >
        <OuterIdea {...{ formData, setFormData }} />
      </Modal>
    </ModalTransition>
  );
};

export default OuterModal;
