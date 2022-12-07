import { useState, useEffect } from "react"
import { Container, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CommonsList from "main/components/Commons/CommonsList";
import { useBackend, useBackendMutation } from "main/utils/useBackend";
import { useCurrentUser } from "main/utils/currentUser";
import Background from './../../assets/HomePageBackground.jpg';

export default function HomePage() {
  // Stryker disable next-line all
  const [commonsJoined, setCommonsJoined] = useState([]);
  const { data: currentUser } = useCurrentUser();
  // Stryker disable all 

  const { data: commons } =
    useBackend(
      ["/api/commons/all"],
      { url: "/api/commons/all" },
      []
    );
  // Stryker enable all 

  const objectToAxiosParams = (newCommonsId) => ({
    url: "/api/commons/join",
    method: "POST",
    params: {
      commonsId: newCommonsId
    }
  });

  const mutation = useBackendMutation(
    objectToAxiosParams,
    {},
    // Stryker disable next-line all : hard to set up test for caching
    ["/api/currentUser"]
  );

  useEffect(
    () => {
      if (currentUser?.root?.user?.commons) {
        setCommonsJoined(currentUser.root.user.commons);
      }
    }, [currentUser]
  );

  let navigate = useNavigate();
  const visitButtonClick = (id) => { navigate("/play/" + id) };

  //create a list of commons that the user hasn't joined for use in the "Join a New Commons" list.
  let joinedIdList = [];
  for (let commonJoined of commonsJoined) {
	joinedIdList.push(commonJoined.id)
  }
  let commonsNotJoined = commons.filter(f => !joinedIdList.includes(f.id));


  return (
    <div style={{ backgroundSize: 'cover', backgroundImage: `url(${Background})` }}>
      <BasicLayout>
        <h1 data-testid="homePage-title" style={{ fontSize: "75px", borderRadius: "7px", backgroundColor: "white", opacity: ".9" }} className="text-center border-0 my-3">Howdy Farmer</h1>
        <Container>
          <Row>
            <Col sm><CommonsList commonList={commonsJoined} title="Visit A Commons" buttonText={"Visit"} buttonLink={visitButtonClick} /></Col>
            <Col sm><CommonsList commonList={commonsNotJoined} title="Join A New Commons" buttonText={"Join"} buttonLink={mutation.mutate} /></Col>
          </Row>
        </Container>
      </BasicLayout>
    </div>
  )
}
