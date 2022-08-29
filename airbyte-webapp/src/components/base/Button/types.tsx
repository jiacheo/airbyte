import React, { MutableRefObject } from "react";

type ButtonSize = "xs" | "sm" | "lg";
type ButtonVariant = "primary" | "secondary" | "danger" | "light";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  buttonRef?: MutableRefObject<HTMLButtonElement | null>;
  variant?: ButtonVariant;
  clickable?: boolean;
  customStyles?: string;
  full?: boolean;
  icon?: React.ReactElement;
  iconPosition?: "left" | "right";
  isLoading?: boolean;
  size?: ButtonSize;
  wasActive?: boolean;
  width?: number;
}
