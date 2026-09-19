DELETE l FROM ledger l
JOIN (
    SELECT ledger_id,
           ROW_NUMBER() OVER (PARTITION BY source_type, source_id ORDER BY ledger_id ASC) AS rn
    FROM ledger
) dup ON l.ledger_id = dup.ledger_id
WHERE dup.rn > 1;

ALTER TABLE ledger ADD CONSTRAINT uk_ledger_source UNIQUE (source_type, source_id);