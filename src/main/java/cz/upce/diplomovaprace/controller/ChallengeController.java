package cz.upce.diplomovaprace.controller;

import cz.upce.diplomovaprace.dto.ChallengeDetailDto;
import cz.upce.diplomovaprace.dto.UserDto;
import cz.upce.diplomovaprace.entity.Challenge;
import cz.upce.diplomovaprace.entity.ChallengeResult;
import cz.upce.diplomovaprace.entity.Game;
import cz.upce.diplomovaprace.entity.GameParam;
import cz.upce.diplomovaprace.entity.ResultState;
import cz.upce.diplomovaprace.entity.User;
import cz.upce.diplomovaprace.enums.ActiveTabConstants;
import cz.upce.diplomovaprace.enums.ResultStateConstants;
import cz.upce.diplomovaprace.manager.SessionManager;
import cz.upce.diplomovaprace.model.ChallengeModel;
import cz.upce.diplomovaprace.model.ChallengeResultModel;
import cz.upce.diplomovaprace.repository.ChallengeRepository;
import cz.upce.diplomovaprace.repository.ChallengeResultRepository;
import cz.upce.diplomovaprace.repository.ChallengeStateRepository;
import cz.upce.diplomovaprace.repository.GameRepository;
import cz.upce.diplomovaprace.repository.RatingRepository;
import cz.upce.diplomovaprace.repository.ResultStateRepository;
import cz.upce.diplomovaprace.repository.UserRepository;
import cz.upce.diplomovaprace.service.ChallengeService;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/challenge")
@SessionAttributes(ActiveTabConstants.ACTIVE_TAB)
public class ChallengeController {

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    SessionManager sessionManager;

    @Autowired
    ChallengeStateRepository challengeStateRepository;

    @Autowired
    ResultStateRepository resultStateRepository;

    @Autowired
    ChallengeResultRepository challengeResultRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    ChallengeService challengeService;

    private static final String IS_USER_ALREADY_IN_CHALLENGE_MODEL_KEY = "isUserAlreadyInChallenge";
    private static final String CHALLENGE_ID_REQUEST_PARAM = "challengeId";

    @GetMapping("/create")
    public ModelAndView renderMap(@RequestParam("latCoords") String latCoords, @RequestParam("lngCoords") String lngCoords,
                                  @ModelAttribute("challengeModel") ChallengeModel challengeModel, BindingResult bindingResult, Map<String, Object> model) {
        model.put(ActiveTabConstants.ACTIVE_TAB, ActiveTabConstants.MAP);
        challengeModel.setLatCoords(latCoords);
        challengeModel.setLngCoords(lngCoords);
        model.put("games", gameRepository.findAll());
        return new ModelAndView("createChallenge", model);
    }

    @GetMapping("/detail")
    public ModelAndView challengeDetail(@RequestParam("challengeId") int challengeId, Map<String, Object> model) throws Exception {
        model.put(ActiveTabConstants.ACTIVE_TAB, ActiveTabConstants.MAP);
        Challenge c = challengeRepository.findById(Integer.valueOf(challengeId)).orElseThrow(Exception::new);
        List<ChallengeResult> challengeResults = challengeResultRepository.findChallengeResultsByChallengeByChallengeId(c);
        // Prepare teams
        List<User> players = challengeResults.stream().map(ChallengeResult::getUserByUserId).collect(Collectors.toList());
        Game game = c.getGameByGameId();

        List<UserDto> userDtos = new ArrayList<>();
        for (User user : players) {
            UserDto userDto = new UserDto();
            int userId = user.getId();
            userDto.setId(userId);
            userDto.setRating(ratingRepository.findByUserByUserIdAndGameByGameId(user, game).getRating());
            userDto.setUserName(user.getUserName());
            int numberOfWins = challengeResultRepository.findChallengeResultsByUserByUserIdAndResultStateByResultStateId(user, resultStateRepository.findById(1).orElseThrow(Exception::new)).size();
            int numberOfLosses = challengeResultRepository.findChallengeResultsByUserByUserIdAndResultStateByResultStateId(user, resultStateRepository.findById(2).orElseThrow(Exception::new)).size();
            int numberOfTies = challengeResultRepository.findChallengeResultsByUserByUserIdAndResultStateByResultStateId(user, resultStateRepository.findById(3).orElseThrow(Exception::new)).size();
            int totalNumberOfGames = numberOfWins + numberOfLosses + numberOfTies;
            userDto.setNumberOfWins(numberOfWins);
            userDto.setNumberOfLosses(numberOfLosses);
            userDto.setNumberOfTies(numberOfTies);
            userDto.setNumberOfGames(totalNumberOfGames);
            ChallengeResult result = challengeResultRepository.findChallengeResultByUserByUserIdAndChallengeByChallengeId(user, c);
            if (!"IN_PROGRESS".equals(result.getResultStateByResultStateId().getState())) {
                userDto.setWinningUserScore(result.getScoreWinner());
                userDto.setLossingUserScore(result.getScoreDefeated());
            } else {
                userDto.setChallengeResultState("IN_PROGRESS");
            }
            userDtos.add(userDto);
        }
        // sort by rating
        userDtos.sort(Comparator.comparingInt(UserDto::getRating).reversed());
        // calculate teams
        List<UserDto> firstTeam = new ArrayList<>();
        List<UserDto> secondTeam = new ArrayList<>();
        for (int i = 0; i < userDtos.size(); i++) {
            if (i % 2 == 0) {
                firstTeam.add(userDtos.get(i));
            } else {
                secondTeam.add(userDtos.get(i));
            }
        }
        // set ChallengeDetailDto
        ChallengeDetailDto challengeDetailDto = new ChallengeDetailDto();
        challengeDetailDto.setUserDtos(userDtos);
        challengeDetailDto.setFirstTeam(firstTeam);
        challengeDetailDto.setSecondTeam(secondTeam);
        Collection<GameParam> gameParams = game.getGameParamsById();
        challengeDetailDto.setMaxPlayers(Integer.parseInt(gameParams.stream().filter(gameParam -> gameParam.getName().equals("number_of_players")).findFirst().orElseThrow(Exception::new).getValue()));


        model.put("challengeDetailDto", challengeDetailDto);
        model.put("players", players);
        model.put("challenge", c);

        model.put(IS_USER_ALREADY_IN_CHALLENGE_MODEL_KEY, challengeService.isUserAlreadyInChallenge(challengeId));
        return new ModelAndView("challengeDetail", model);
    }


    @GetMapping("/enterResult")
    public ModelAndView challengeEnterResult(@RequestParam("challengeId") Integer challengeId,
                                             @ModelAttribute("challengeResultModel") ChallengeResultModel challengeResultModel,
                                             BindingResult bindingResult, Map<String, Object> model) throws Exception {
        model.put(ActiveTabConstants.ACTIVE_TAB, ActiveTabConstants.MAP);
        model.put("challenge", challengeRepository.findById(challengeId).orElseThrow(Exception::new));
        return new ModelAndView("challengeResult", model);
    }

    @PostMapping("/submitResult")
    public ModelAndView challengeSubmitResult(@RequestParam("challengeId") String challengeId,
                                              @ModelAttribute("challengeResultModel") ChallengeResultModel challengeResultModel,
                                              BindingResult bindingResult, Map<String, Object> model) throws Exception {
        Challenge challenge = challengeRepository.findById(Integer.valueOf(challengeId)).orElseThrow(Exception::new);
        User user = userRepository.findById(sessionManager.getUserId()).orElseThrow(Exception::new);
        ChallengeResult challengeResult = challengeResultRepository.findChallengeResultByUserByUserIdAndChallengeByChallengeId(user, challenge);
        int winnerScore;
        int looserScore;
        int resultStateId;
        switch (challengeResultModel.getResultState()) {
            case "WINNER":
                resultStateId = 1;
                winnerScore = challengeResultModel.getScoreTeam1();
                looserScore = challengeResultModel.getScoreTeam2();
                break;
            case "DEFEATED":
                resultStateId = 2;
                winnerScore = challengeResultModel.getScoreTeam2();
                looserScore = challengeResultModel.getScoreTeam1();
                break;
            case "TIE":
                resultStateId = 3;
                winnerScore = challengeResultModel.getScoreTeam1();
                looserScore = challengeResultModel.getScoreTeam2();
                break;
            default:
                throw new Exception();
        }

        challengeResult.setResultStateByResultStateId(resultStateRepository.findById(resultStateId).orElseThrow(Exception::new));
        challengeResult.setScoreWinner(winnerScore);
        challengeResult.setScoreDefeated(looserScore);
        challengeResultRepository.save(challengeResult);

        return new ModelAndView("redirect:/challenge/detail?challengeId=" + challengeId, model);
    }

    @PostMapping("/create")
    public ModelAndView renderMaps(@ModelAttribute("challengeModel") ChallengeModel challengeModel, BindingResult bindingResult,
                                   Map<String, Object> model) throws Exception {
        Challenge challenge = new Challenge();
        challenge.setChallengeStateByChallengeStateId(challengeStateRepository.findById(1).orElseThrow(Exception::new));
        challenge.setGameByGameId(gameRepository.findById(challengeModel.getGameId()).orElseThrow(Exception::new));
        challenge.setCreated(Timestamp.valueOf("1992-04-10 10:10:11"));
        challenge.setStart(Timestamp.valueOf("1992-04-10 10:10:11"));
        challenge.setEnd(Timestamp.valueOf("1992-04-10 10:10:11"));
        challenge.setCoordsLat(challengeModel.getLatCoords());
        challenge.setCoordsLng(challengeModel.getLngCoords());
        challenge.setDescription("Description");
        challenge.setPassword("pass");
        challengeRepository.save(challenge);
        ChallengeResult challengeResult = new ChallengeResult();
        challengeResult.setChallengeByChallengeId(challenge);
        challengeResult.setUserByUserId(userRepository.findById(sessionManager.getUserId()).orElseThrow(Exception::new));
        challengeResult.setResultStateByResultStateId(resultStateRepository.findById(4).orElseThrow(Exception::new));
        challengeResult.setCreated(Timestamp.valueOf("1992-04-10 10:10:11"));
        challengeResultRepository.save(challengeResult);
        return new ModelAndView("redirect:/map", model);
    }

    @GetMapping("/join")
    public ModelAndView challengeJoin(@RequestParam(CHALLENGE_ID_REQUEST_PARAM) int challengeId,
                                      RedirectAttributes redirectAttributes) throws Exception {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(Exception::new);
        User user = userRepository.findById(sessionManager.getUserId()).orElseThrow(Exception::new);
        ResultState resultState = resultStateRepository.findResultStateByState(ResultStateConstants.IN_PROGRESS);

        ChallengeResult challengeResult = new ChallengeResult();
        challengeResult.setChallengeByChallengeId(challenge);
        challengeResult.setUserByUserId(user);
        challengeResult.setResultStateByResultStateId(resultState);
        challengeResultRepository.save(challengeResult);

        return redirectToChallengeDetail(challengeId, redirectAttributes);
    }

    @GetMapping("/logout")
    public ModelAndView challengeLogout(@RequestParam(CHALLENGE_ID_REQUEST_PARAM) int challengeId,
                                        RedirectAttributes redirectAttributes) throws Exception {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(Exception::new);
        User user = userRepository.findById(sessionManager.getUserId()).orElseThrow(Exception::new);
        ChallengeResult challengeResult = challengeResultRepository.findChallengeResultByUserByUserIdAndChallengeByChallengeId(user, challenge);
        challengeResultRepository.delete(challengeResult);

        return redirectToChallengeDetail(challengeId, redirectAttributes);
    }

    private ModelAndView redirectToChallengeDetail(@NonNull int challengeId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(CHALLENGE_ID_REQUEST_PARAM, challengeId);
        return new ModelAndView("redirect:/challenge/detail");
    }
}
