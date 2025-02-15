import className from "classnames";
import React from "react";
import { FormattedMessage } from "react-intl";

import { Text } from "components/base/Text";
import { InfoTooltip } from "components/base/Tooltip";
import Label from "components/Label";

import styles from "./ControlLabels.module.scss";

export interface ControlLabelsProps {
  className?: string;
  error?: boolean;
  success?: boolean;
  nextLine?: boolean;
  message?: React.ReactNode;
  labelAdditionLength?: number;
  label?: React.ReactNode;
  infoTooltipContent?: React.ReactNode;
  optional?: boolean;
  htmlFor?: string;
}

const ControlLabels: React.FC<React.PropsWithChildren<ControlLabelsProps>> = (props) => (
  <div className={className(styles.controlContainer, props.className)}>
    <Label
      error={props.error}
      success={props.success}
      message={props.message}
      additionLength={props.labelAdditionLength}
      nextLine={props.nextLine}
      htmlFor={props.htmlFor}
    >
      {props.label}
      {props.infoTooltipContent && (
        <InfoTooltip className={styles.tooltip} placement="top-start">
          {props.infoTooltipContent}
        </InfoTooltip>
      )}
      {props.optional && (
        <Text size="sm" className={styles.optionalText}>
          <FormattedMessage id="form.optional" />
        </Text>
      )}
    </Label>
    {props.children}
  </div>
);

export { ControlLabels };
