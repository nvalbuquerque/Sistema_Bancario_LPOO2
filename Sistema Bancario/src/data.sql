INSERT IGNORE INTO CLIENTE (id, nome, sobrenome, rg, cpf, endereco) VALUES 
(1, 'Ana', 'Silva', '11111111', '12345678901', 'Rua das Flores, 123'),
(2, 'Bruno', 'Souza', '22222222', '98765432100', 'Av. Central, 456'),
(3, 'Carlos', 'Oliveira', '33333333', '45678912311', 'Alameda Verde, 789'),
(4, 'Gabriel', 'Bezerra', '44444444', '56789012311', 'Av. Brasil, 750');

INSERT IGNORE INTO CONTA (id, numero, saldo, tipo, cliente_id) VALUES 
(10, 1001, 1500.50, 'CORRENTE', 1), 
(20, 2002, 50000.00, 'INVESTIMENTO', 2),
(30, 3003, 350.00, 'CORRENTE', 3),
(40, 4004, 500.00, 'INVESTIMENTO', 4);

INSERT IGNORE INTO CONTA_CORRENTE (conta_id, limite) VALUES 
(10, 1000.00),
(30, 200.00);

INSERT IGNORE INTO CONTA_INVESTIMENTO (conta_id, montante_minimo, deposito_minimo) VALUES 
(20, 5000.00, 500.00),
(40, 100.00, 10.00);
