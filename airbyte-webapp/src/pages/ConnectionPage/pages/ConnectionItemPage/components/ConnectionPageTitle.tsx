import { faTrash } from "@fortawesome/free-solid-svg-icons";
import React, { useCallback, useMemo } from "react";
import { FormattedMessage } from "react-intl";
import { useNavigate } from "react-router-dom";

import { Text } from "components/base/Text";
import { InfoBox } from "components/ui/InfoBox";
import { StepsMenu } from "components/ui/StepsMenu";

import { ConnectionStatus, DestinationRead, SourceRead, WebBackendConnectionRead } from "core/request/AirbyteClient";

import { ConnectionSettingsRoutes } from "../ConnectionSettingsRoutes";
import ConnectionName from "./ConnectionName";
import styles from "./ConnectionPageTitle.module.scss";
import { StatusMainInfo } from "./StatusMainInfo";

interface ConnectionPageTitleProps {
  source: SourceRead;
  destination: DestinationRead;
  connection: WebBackendConnectionRead;
  currentStep: ConnectionSettingsRoutes;
  onStatusUpdating?: (updating: boolean) => void;
}

const ConnectionPageTitle: React.FC<ConnectionPageTitleProps> = ({
  source,
  destination,
  connection,
  currentStep,
  onStatusUpdating,
}) => {
  const navigate = useNavigate();

  const steps = useMemo(() => {
    const steps = [
      {
        id: ConnectionSettingsRoutes.STATUS,
        name: <FormattedMessage id="sources.status" />,
      },
      {
        id: ConnectionSettingsRoutes.REPLICATION,
        name: <FormattedMessage id="connection.replication" />,
      },
      {
        id: ConnectionSettingsRoutes.TRANSFORMATION,
        name: <FormattedMessage id="connectionForm.transformation.title" />,
      },
    ];

    connection.status !== ConnectionStatus.deprecated &&
      steps.push({
        id: ConnectionSettingsRoutes.SETTINGS,
        name: <FormattedMessage id="sources.settings" />,
      });

    return steps;
  }, [connection.status]);

  const onSelectStep = useCallback(
    (id: string) => {
      if (id === ConnectionSettingsRoutes.STATUS) {
        navigate("");
      } else {
        navigate(id);
      }
    },
    [navigate]
  );

  return (
    <div className={styles.container}>
      {connection.status === ConnectionStatus.deprecated && (
        <InfoBox className={styles.connectionDeleted} icon={faTrash}>
          <FormattedMessage id="connection.connectionDeletedView" />
        </InfoBox>
      )}
      <Text as="div" centered bold className={styles.connectionTitle}>
        <FormattedMessage id="connection.title" />
      </Text>
      <ConnectionName connection={connection} />
      <div className={styles.statusContainer}>
        <StatusMainInfo
          connection={connection}
          source={source}
          destination={destination}
          onStatusUpdating={onStatusUpdating}
        />
      </div>
      <StepsMenu lightMode data={steps} onSelect={onSelectStep} activeStep={currentStep} />
    </div>
  );
};

export default ConnectionPageTitle;
