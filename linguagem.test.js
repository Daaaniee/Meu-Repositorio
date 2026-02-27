const test = require('node:test');
const assert = require('node:assert/strict');

const { linguagemDosGatosECachorros } = require('./linguagem');

const cenarios = [
  {
    nome: 'deve retornar "au au" quando o animal for cachorro',
    entrada: 'cachorro',
    esperado: 'au au',
  },
  {
    nome: 'deve retornar "miau" quando o animal for gato',
    entrada: 'gato',
    esperado: 'miau',
  },
  {
    nome: 'deve retornar mensagem de orientação para um animal não suportado',
    entrada: 'papagaio',
    esperado: 'Informe gato ou cachorro',
  },
  {
    nome: 'deve retornar mensagem de orientação para texto vazio',
    entrada: '',
    esperado: 'Informe gato ou cachorro',
  },
  {
    nome: 'deve retornar mensagem de orientação para valor nulo',
    entrada: null,
    esperado: 'Informe gato ou cachorro',
  },
  {
    nome: 'deve retornar mensagem de orientação para valor indefinido',
    entrada: undefined,
    esperado: 'Informe gato ou cachorro',
  },
  {
    nome: 'deve diferenciar letras maiúsculas e minúsculas',
    entrada: 'GATO',
    esperado: 'Informe gato ou cachorro',
  },
];

for (const cenario of cenarios) {
  test(cenario.nome, () => {
    assert.equal(linguagemDosGatosECachorros(cenario.entrada), cenario.esperado);
  });
}
