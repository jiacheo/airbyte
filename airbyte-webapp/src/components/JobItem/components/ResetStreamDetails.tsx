import classNames from "classnames";
import React from "react";

import { Text } from "components/base/Text";

import styles from "./ResetStreamDetails.module.scss";

interface ResetStreamsDetailsProps {
  names?: string[];
  isOpen?: boolean;
}

export const ResetStreamsDetails: React.FC<ResetStreamsDetailsProps> = ({ names = [], isOpen }) => (
  <Text as="p" size="sm" className={classNames(styles.textContainer, { [styles.open]: isOpen })}>
    {names.map((name) => (
      <span key={name} className={styles.text}>
        {name}
      </span>
    ))}
  </Text>
);
