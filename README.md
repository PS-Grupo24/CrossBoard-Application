# CrossBoard-Application

Projeto desenvolvido no Instituto Superior de Engenharia de Lisboa (ISEL) no âmbito da unidade curricular de Projeto e Seminário no ano curricular 2024/2025 com o regente Engenheiro Fernando Sousa. O projeto teve a supervisão do orientador Engenheiro Pedro Pereira, sendo composto o grupo pelos elementos:
- Rúben Louro 48926 (A48926@alunos.isel.pt)
- Luís Reis 48318 (A48318@alunos.isel.pt)

O projeto CrossBoard Application pode ser acedido online pelo link: **https://crossboard.onrender.com/**.

Os instaladores para Android ou Windows encontram-se na pasta [install](./install).

Para o desenvolvimento de novos jogos para a aplicação deve ser seguido o seguinte [guia de Novo Jogo](./docs/GuiaNovoJogo.md).
## Introdução

O projeto CrossBoard Application é uma aplicação que visa explorar as funcionalidades da tecnologia Kotlin Multiplatform, a qual tem como objetivo o desenvolvimento de aplicações multiplataforma, maximizando a partilha de código entre cliente e o servidor, e facilitando a manutenção.

No nosso quotidiano, é evidente a existência de muitas aplicações comuns entre diferentes plataformas, como o computador, smartphone ou browser. No entanto, ao serem desenvolvidas especificamente para uma determinada plataforma, a sua adaptação a outras requer, muitas vezes, o desenvolvimento de diferentes aplicações distintas, aumentando significativamente o custo e a complexidade do processo. A utilização de tecnologias como o Kotlin Multiplatform visa facilitar esse desenvolvimento.

O projeto CrossBoard Application consiste numa aplicação multiplataforma de jogos de tabuleiro (computador, smartphone, browser) demonstrando como a utilização de interfaces e reutilização de código facilitará a adição de novas funcionalidades (neste caso de jogos). No caso desta aplicação, tal será exemplificado através da implementação de dois tipos diferentes de jogos de tabuleiro, sendo o TicTacToe [2] e o Reversi [3].

O utilizador pode realizar diversas ações na aplicação, sendo possível utilizar a aplicação sem registo, jogando em modo Singleplayer (Jogo contra o computador), ou, no caso opte por registar-se, tem acesso a funcionalidades adicionais, como jogar contra outros jogadores em diferentes plataformas.

Em suma, a aplicação CrossBoard Application pretende demonstrar o potencial do Kotlin Multiplatform, integrando diversas funcionalidades e jogos, os quais serão apresentados posteriormente. Neste capítulo, serão ainda apresentados a motivação para o desenvolvimento do projeto, os objetivos e especificações do mesmo, bem como a estrutura do relatório.

## Agradecimentos

No desenvolvimento do nosso projeto, queremos expressar o nosso agradecimento ao nosso orientador, Engenheiro Pedro Pereira, pela oportunidade de realizarmos um projeto sobre um tema do nosso interesse. Agradecemos também por nos ter apresentado a tecnologia Kotlin Multiplatform, que contribui para uma experiência mais enriquecedora ao longo do projeto e nos permitiu adquirir um maior conhecimento sobre tecnologias aplicáveis ao desenvolvimento de aplicações desta natureza. A sua disponibilidade, exigência e compromisso foram fundamentais para o sucesso deste trabalho, e por isso, deixamos o nosso sincero agradecimento.

Estendemos também o nosso reconhecimento ao Engenheiro Fernando Sousa, pelo seu excelente trabalho e dedicação na disciplina de Projeto e Seminário, proporcionando-nos uma experiência positiva, bem como uma organização exemplar da unidade curricular.

Por fim, agradecemos ao Instituto Superior de Engenharia  de Lisboa pela oportunidade e pelos recursos disponibilizados ao longo da disciplina de Projeto e Seminário. Agradecemos igualmente a todos os docentes que nos acompanharam durante o percurso académico, contribuindo para um ambiente de aprendizagem estimulante e para o desenvolvimento das nossas competências na área da Engenharia Informática, preparando-nos de forma sólida para os desafios da vida profissional.


