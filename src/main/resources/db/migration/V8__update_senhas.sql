UPDATE usuarios SET senha_hash = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'
WHERE email IN (
                'admin@delivery.com',
                'joao@email.com',
                'maria@email.com',
                'burguer@delivery.com',
                'pizza@delivery.com'
    );