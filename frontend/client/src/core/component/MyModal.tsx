import { createAnimation, IonButton, IonIcon, IonModal } from "@ionic/react";
import { useState } from "react";
import PropTypes from 'prop-types';

interface MyModalProps {
    children: PropTypes.ReactNodeLike;
    openModalIcon: string;
    closeModalIcon: string;
}

export const MyModal: React.FC<MyModalProps> = ({ children, openModalIcon, closeModalIcon }) => {
    const [showModal, setShowModal] = useState(false);

    const enterAnimation = (baseEl: any) => {
        const backdropAnimation = createAnimation()
            .addElement(baseEl.querySelector('ion-backdrop')!)
            .fromTo('opacity', '0.01', 'var(--backdrop-opacity)');

        const wrapperAnimation = createAnimation()
            .addElement(baseEl.querySelector('.modal-wrapper')!)
            .keyframes([
                { offset: 0, opacity: '0', transform: 'scale(0)' },
                { offset: 1, opacity: '0.99', transform: 'scale(1)' }
            ]);

        return createAnimation()
            .addElement(baseEl)
            .easing('ease-out')
            .duration(500)
            .addAnimation([backdropAnimation, wrapperAnimation]);
    }

    const leaveAnimation = (baseEl: any) => {
        return enterAnimation(baseEl).direction('reverse');
    }

    return (
        <>
            <IonModal isOpen={showModal} enterAnimation={enterAnimation} leaveAnimation={leaveAnimation}>
                {children}
                <IonButton onClick={() => setShowModal(false)}>
                    <IonIcon icon={closeModalIcon} />
                </IonButton>
            </IonModal>
            <IonButton onClick={() => setShowModal(true)}>
                <IonIcon icon={openModalIcon} />
            </IonButton>
        </>
    );
};
