import { useEffect, useRef, useState } from "react";

export default function usePullToRefresh(onRefresh) {
    const [pulling, setPulling] = useState(false);
    const [pullDistance, setPullDistance] = useState(0);
    const startY = useRef(0);
    const isAtTop = useRef(true);

    useEffect(() => {
        function handleTouchStart(e) {
            isAtTop.current = window.scrollY === 0;
            if (isAtTop.current) {
                startY.current = e.touches[0].clientY;
            }
        }

        function handleTouchMove(e) {
            if (!isAtTop.current) return;
            const diff = e.touches[0].clientY - startY.current;
            if (diff > 0 && window.scrollY === 0) {
                setPullDistance(Math.min(diff, 80));
            }
        }

        async function handleTouchEnd() {
            if (pullDistance > 60) {
                setPulling(true);
                await onRefresh();
                setPulling(false);
            }
            setPullDistance(0);
        }

        document.addEventListener("touchstart", handleTouchStart, { passive: true });
        document.addEventListener("touchmove", handleTouchMove, { passive: true });
        document.addEventListener("touchend", handleTouchEnd);

        return () => {
            document.removeEventListener("touchstart", handleTouchStart);
            document.removeEventListener("touchmove", handleTouchMove);
            document.removeEventListener("touchend", handleTouchEnd);
        };
    }, [pullDistance, onRefresh]);

    return { pulling, pullDistance };
}